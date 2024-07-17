/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.jelilio.i18nresourcebundle.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Utility methods for resolving resource locations to files in the
 * file system. Mainly for internal use within the framework.
 *
 */
public abstract class ResourceUtils {

  /** Pseudo URL prefix for loading from the class path: "classpath:". */
  public static final String CLASSPATH_URL_PREFIX = "classpath:";

  /** URL prefix for loading from the file system: "file:". */
  public static final String FILE_URL_PREFIX = "file:";

  /** URL prefix for loading from a jar file: "jar:". */
  public static final String JAR_URL_PREFIX = "jar:";

  /** URL prefix for loading from a war file on Tomcat: "war:". */
  public static final String WAR_URL_PREFIX = "war:";

  /** URL protocol for a file in the file system: "file". */
  public static final String URL_PROTOCOL_FILE = "file";

  /** URL protocol for an entry from a jar file: "jar". */
  public static final String URL_PROTOCOL_JAR = "jar";

  /** URL protocol for an entry from a war file: "war". */
  public static final String URL_PROTOCOL_WAR = "war";

  /** URL protocol for an entry from a zip file: "zip". */
  public static final String URL_PROTOCOL_ZIP = "zip";

  /** URL protocol for an entry from a WebSphere jar file: "wsjar". */
  public static final String URL_PROTOCOL_WSJAR = "wsjar";

  /** URL protocol for an entry from a JBoss jar file: "vfszip". */
  public static final String URL_PROTOCOL_VFSZIP = "vfszip";

  /** URL protocol for a JBoss file system resource: "vfsfile". */
  public static final String URL_PROTOCOL_VFSFILE = "vfsfile";

  /** URL protocol for a general JBoss VFS resource: "vfs". */
  public static final String URL_PROTOCOL_VFS = "vfs";

  /** File extension for a regular jar file: ".jar". */
  public static final String JAR_FILE_EXTENSION = ".jar";

  /** Separator between JAR URL and file path within the JAR: "!/". */
  public static final String JAR_URL_SEPARATOR = "!/";

  /** Special separator between WAR URL and jar part on Tomcat. */
  public static final String WAR_URL_SEPARATOR = "*/";

  /**
   * Create a URI instance for the given URL,
   * replacing spaces with "%20" URI encoding first.
   * @param url the URL to convert into a URI instance
   * @return the URI instance
   * @throws URISyntaxException if the URL wasn't a valid URI
   * @see java.net.URL#toURI()
   * @see #toURI(String)
   */
  public static URI toURI(URL url) throws URISyntaxException {
    return toURI(url.toString());
  }

  /**
   * Create a URI instance for the given location String,
   * replacing spaces with "%20" URI encoding first.
   * @param location the location String to convert into a URI instance
   * @return the URI instance
   * @throws URISyntaxException if the location wasn't a valid URI
   * @see #toURI(URL)
   */
  public static URI toURI(String location) throws URISyntaxException {
    return new URI(StringUtils.replace(location, " ", "%20"));
  }


  /**
   * Determine whether the given URL points to a resource in the file system,
   * i.e. has protocol "file", "vfsfile" or "vfs".
   * @param url the URL to check
   * @return whether the URL has been identified as a file system URL
   * @see #isJarURL(URL)
   */
  public static boolean isFileURL(URL url) {
    String protocol = url.getProtocol();
    return (URL_PROTOCOL_FILE.equals(protocol) || URL_PROTOCOL_VFSFILE.equals(protocol) ||
        URL_PROTOCOL_VFS.equals(protocol));
  }

  /**
   * Determine whether the given URL points to a resource in a jar file
   * &mdash; for example, whether the URL has protocol "jar", "war, "zip",
   * "vfszip", or "wsjar".
   * @param url the URL to check
   * @return whether the URL has been identified as a JAR URL
   */
  public static boolean isJarURL(URL url) {
    String protocol = url.getProtocol();
    return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_WAR.equals(protocol) ||
        URL_PROTOCOL_ZIP.equals(protocol) || URL_PROTOCOL_VFSZIP.equals(protocol) ||
        URL_PROTOCOL_WSJAR.equals(protocol));
  }

  /**
   * Set the {@link URLConnection#setUseCaches "useCaches"} flag on the
   * given connection, preferring {@code false} but leaving the flag at
   * its JVM default value for jar resources (typically {@code true}).
   * @param con the URLConnection to set the flag on
   * @see URLConnection#setUseCaches
   */
  public static void useCachesIfNecessary(URLConnection con) {
    if (!(con instanceof JarURLConnection)) {
      con.setUseCaches(false);
    }
  }

  /**
   * Extract the URL for the outermost archive from the given jar/war URL
   * (which may point to a resource in a jar file or to a jar file itself).
   * <p>In the case of a jar file nested within a war file, this will return
   * a URL to the war file since that is the one resolvable in the file system.
   * @param jarUrl the original URL
   * @return the URL for the actual jar file
   * @throws MalformedURLException if no valid jar file URL could be extracted
   * @since 4.1.8
   * @see #extractJarFileURL(URL)
   */
  public static URL extractArchiveURL(URL jarUrl) throws MalformedURLException {
    String urlFile = jarUrl.getFile();

    int endIndex = urlFile.indexOf(WAR_URL_SEPARATOR);
    if (endIndex != -1) {
      // Tomcat's "war:file:...mywar.war*/WEB-INF/lib/myjar.jar!/myentry.txt"
      String warFile = urlFile.substring(0, endIndex);
      if (URL_PROTOCOL_WAR.equals(jarUrl.getProtocol())) {
        return toURL(warFile);
      }
      int startIndex = warFile.indexOf(WAR_URL_PREFIX);
      if (startIndex != -1) {
        return toURL(warFile.substring(startIndex + WAR_URL_PREFIX.length()));
      }
    }

    // Regular "jar:file:...myjar.jar!/myentry.txt"
    return extractJarFileURL(jarUrl);
  }

  public static URL toURL(String location) throws MalformedURLException {
    try {
      return toURI(StringUtils.cleanPath(location)).toURL();
    } catch (IllegalArgumentException | URISyntaxException var2) {
      return new URL(location);
    }
  }

  public static URL toRelativeURL(URL root, String relativePath) throws MalformedURLException {
    relativePath = StringUtils.replace(relativePath, "#", "%23");
    return toURL(StringUtils.applyRelativePath(root.toString(), relativePath));
  }

  /**
   * Extract the URL for the actual jar file from the given URL
   * (which may point to a resource in a jar file or to a jar file itself).
   * @param jarUrl the original URL
   * @return the URL for the actual jar file
   * @throws MalformedURLException if no valid jar file URL could be extracted
   * @see #extractArchiveURL(URL)
   */
  public static URL extractJarFileURL(URL jarUrl) throws MalformedURLException {
    String urlFile = jarUrl.getFile();
    int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
    if (separatorIndex != -1) {
      String jarFile = urlFile.substring(0, separatorIndex);
      try {
        return toURL(jarFile);
      }
      catch (MalformedURLException ex) {
        // Probably no protocol in original jar URL, like "jar:C:/mypath/myjar.jar".
        // This usually indicates that the jar file resides in the file system.
        if (!jarFile.startsWith("/")) {
          jarFile = "/" + jarFile;
        }
        return toURL(FILE_URL_PREFIX + jarFile);
      }
    }
    else {
      return jarUrl;
    }
  }

  public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
    Assert.notNull(resourceUrl, "Resource URL must not be null");
    if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
      throw new FileNotFoundException(
          description + " cannot be resolved to absolute file path " +
              "because it does not reside in the file system: " + resourceUrl);
    }
    try {
      // URI decoding for special characters such as spaces.
      return new File(toURI(resourceUrl).getSchemeSpecificPart());
    }
    catch (URISyntaxException ex) {
      // Fallback for URLs that are not valid URIs (should hardly ever happen).
      return new File(resourceUrl.getFile());
    }
  }

  public static File getFile(URI resourceUri, String description) throws FileNotFoundException {
    Assert.notNull(resourceUri, "Resource URI must not be null");
    if (!URL_PROTOCOL_FILE.equals(resourceUri.getScheme())) {
      throw new FileNotFoundException(
          description + " cannot be resolved to absolute file path " +
              "because it does not reside in the file system: " + resourceUri);
    }
    return new File(resourceUri.getSchemeSpecificPart());
  }
}

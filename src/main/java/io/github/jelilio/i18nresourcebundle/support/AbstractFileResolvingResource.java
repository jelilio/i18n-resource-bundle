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

package io.github.jelilio.i18nresourcebundle.support;

import io.github.jelilio.i18nresourcebundle.Resource;
import io.github.jelilio.i18nresourcebundle.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.util.jar.JarEntry;

import static io.github.jelilio.i18nresourcebundle.util.ReflectionUtils.URL_PROTOCOL_VFS;

/**
 * Abstract base class for resources which resolve URLs into File references,
 * such as {@link UrlResource} or {@link ClassPathResource}.
 *
 * <p>Detects the "file" protocol as well as the JBoss "vfs" protocol in URLs,
 * resolving file system references accordingly.
 *
 */
public abstract class AbstractFileResolvingResource extends AbstractResource {
  public AbstractFileResolvingResource() { }

  @Override
  public boolean exists() {
    try {
      URL url = getURL();
      if (ResourceUtils.isFileURL(url)) {
        // Proceed with file system resolution
        return getFile().exists();
      }
      else {
        // Try a URL connection content-length header
        URLConnection con = url.openConnection();
        customizeConnection(con);
        HttpURLConnection httpCon = (con instanceof HttpURLConnection huc ? huc : null);
        if (httpCon != null) {
          httpCon.setRequestMethod("HEAD");
          int code = httpCon.getResponseCode();
          if (code == HttpURLConnection.HTTP_OK) {
            return true;
          }
          else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
            return false;
          }
        }
        if (con.getContentLengthLong() > 0) {
          return true;
        }
        if (httpCon != null) {
          // No HTTP OK status, and no content-length header: give up
          httpCon.disconnect();
          return false;
        }
        else {
          // Fall back to stream existence: can we open the stream?
          getInputStream().close();
          return true;
        }
      }
    }
    catch (IOException ex) {
      return false;
    }
  }

  public boolean isReadable() {
    try {
      return this.checkReadable(this.getURL());
    } catch (IOException var2) {
      return false;
    }
  }

  boolean checkReadable(URL url) {
    try {
      if (ResourceUtils.isFileURL(url)) {
        // Proceed with file system resolution
        File file = getFile();
        return (file.canRead() && !file.isDirectory());
      }
      else {
        // Try InputStream resolution for jar resources
        URLConnection con = url.openConnection();
        customizeConnection(con);
        if (con instanceof HttpURLConnection httpCon) {
          httpCon.setRequestMethod("HEAD");
          int code = httpCon.getResponseCode();
          if (code != HttpURLConnection.HTTP_OK) {
            httpCon.disconnect();
            return false;
          }
        }
        else if (con instanceof JarURLConnection jarCon) {
          JarEntry jarEntry = jarCon.getJarEntry();
          if (jarEntry == null) {
            return false;
          }
          else {
            return !jarEntry.isDirectory();
          }
        }
        long contentLength = con.getContentLengthLong();
        if (contentLength > 0) {
          return true;
        }
        else if (contentLength == 0) {
          // Empty file or directory -> not considered readable...
          return false;
        }
        else {
          // Fall back to stream existence: can we open the stream?
          getInputStream().close();
          return true;
        }
      }
    }
    catch (IOException ex) {
      return false;
    }
  }

  public boolean isFile() {
    try {
      URL url = this.getURL();
      return url.getProtocol().startsWith(URL_PROTOCOL_VFS) ? VfsResourceDelegate.getResource(url).isFile() : "file".equals(url.getProtocol());
    } catch (IOException var2) {
      return false;
    }
  }

  public File getFile() throws IOException {
    URL url = this.getURL();
    return url.getProtocol().startsWith("vfs") ? VfsResourceDelegate.getResource(url).getFile() : ResourceUtils.getFile(url, this.getDescription());
  }

  @Override
  protected File getFileForLastModifiedCheck() throws IOException {
    URL url = getURL();
    if (ResourceUtils.isJarURL(url)) {
      URL actualUrl = ResourceUtils.extractArchiveURL(url);
      if (actualUrl.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
        return VfsResourceDelegate.getResource(actualUrl).getFile();
      }
      return ResourceUtils.getFile(actualUrl, "Jar URL");
    }
    else {
      return getFile();
    }
  }
  protected boolean isFile(URI uri) {
    try {
      return uri.getScheme().startsWith("vfs") ? VfsResourceDelegate.getResource(uri).isFile() : "file".equals(uri.getScheme());
    } catch (IOException var3) {
      return false;
    }
  }

  protected File getFile(URI uri) throws IOException {
    return uri.getScheme().startsWith("vfs") ? VfsResourceDelegate.getResource(uri).getFile() : ResourceUtils.getFile(uri, this.getDescription());
  }

  public ReadableByteChannel readableChannel() throws IOException {
    try {
      return FileChannel.open(this.getFile().toPath(), StandardOpenOption.READ);
    } catch (NoSuchFileException | FileNotFoundException var2) {
      return super.readableChannel();
    }
  }

  @Override
  public long contentLength() throws IOException {
    URL url = getURL();
    if (ResourceUtils.isFileURL(url)) {
      // Proceed with file system resolution
      File file = getFile();
      long length = file.length();
      if (length == 0L && !file.exists()) {
        throw new FileNotFoundException(getDescription() +
            " cannot be resolved in the file system for checking its content length");
      }
      return length;
    }
    else {
      // Try a URL connection content-length header
      URLConnection con = url.openConnection();
      customizeConnection(con);
      if (con instanceof HttpURLConnection httpCon) {
        httpCon.setRequestMethod("HEAD");
      }
      return con.getContentLengthLong();
    }
  }

  @Override
  public long lastModified() throws IOException {
    URL url = getURL();
    boolean fileCheck = false;
    if (ResourceUtils.isFileURL(url) || ResourceUtils.isJarURL(url)) {
      // Proceed with file system resolution
      fileCheck = true;
      try {
        File fileToCheck = getFileForLastModifiedCheck();
        long lastModified = fileToCheck.lastModified();
        if (lastModified > 0L || fileToCheck.exists()) {
          return lastModified;
        }
      }
      catch (FileNotFoundException ex) {
        // Defensively fall back to URL connection check instead
      }
    }
    // Try a URL connection last-modified header
    URLConnection con = url.openConnection();
    customizeConnection(con);
    if (con instanceof HttpURLConnection httpCon) {
      httpCon.setRequestMethod("HEAD");
    }
    long lastModified = con.getLastModified();
    if (fileCheck && lastModified == 0 && con.getContentLengthLong() <= 0) {
      throw new FileNotFoundException(getDescription() +
          " cannot be resolved in the file system for checking its last-modified timestamp");
    }
    return lastModified;
  }

  protected void customizeConnection(URLConnection con) throws IOException {
    ResourceUtils.useCachesIfNecessary(con);
    if (con instanceof HttpURLConnection httpConn) {
      this.customizeConnection(httpConn);
    }

  }

  /**
   * Customize the given {@link HttpURLConnection} before fetching the resource.
   * <p>Can be overridden in subclasses for configuring request headers and timeouts.
   * @param con the HttpURLConnection to customize
   * @throws IOException if thrown from HttpURLConnection methods
   */
  protected void customizeConnection(HttpURLConnection con) throws IOException {
  }

  /**
   * Inner delegate class, avoiding a hard JBoss VFS API dependency at runtime.
   */
  private static class VfsResourceDelegate {

    public static Resource getResource(URL url) throws IOException {
      return new VfsResource(VfsUtils.getRoot(url));
    }

    public static Resource getResource(URI uri) throws IOException {
      return new VfsResource(VfsUtils.getRoot(uri));
    }
  }
}


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
import io.github.jelilio.i18nresourcebundle.annotation.Nullable;
import io.github.jelilio.i18nresourcebundle.util.Assert;
import io.github.jelilio.i18nresourcebundle.util.ResourceUtils;
import io.github.jelilio.i18nresourcebundle.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * {@link Resource} implementation for {@code java.net.URL} locators.
 * Supports resolution as a {@code URL} and also as a {@code File} in
 * case of the {@code "file:"} protocol.
 *
 * @see java.net.URL
 */
public class UrlResource extends AbstractFileResolvingResource {
  private static final String AUTHORIZATION = "Authorization";
  @Nullable
  private final URI uri;
  private final URL url;
  @Nullable
  private volatile String cleanedUrl;

  public UrlResource(URL url) {
    Assert.notNull(url, "URL must not be null");
    this.uri = null;
    this.url = url;
  }

  public UrlResource(URI uri) throws MalformedURLException {
    Assert.notNull(uri, "URI must not be null");
    this.uri = uri;
    this.url = uri.toURL();
  }

  public UrlResource(String path) throws MalformedURLException {
    Assert.notNull(path, "Path must not be null");
    String cleanedPath = StringUtils.cleanPath(path);

    URI uri;
    URL url;
    try {
      uri = ResourceUtils.toURI(cleanedPath);
      url = uri.toURL();
    } catch (IllegalArgumentException | URISyntaxException var6) {
      uri = null;
      url = ResourceUtils.toURL(path);
    }

    this.uri = uri;
    this.url = url;
    this.cleanedUrl = cleanedPath;
  }

  public UrlResource(String protocol, String location) throws MalformedURLException {
    this(protocol, location, (String)null);
  }

  public UrlResource(String protocol, String location, @Nullable String fragment) throws MalformedURLException {
    try {
      this.uri = new URI(protocol, location, fragment);
      this.url = this.uri.toURL();
    } catch (URISyntaxException var6) {
      URISyntaxException ex = var6;
      MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
      exToThrow.initCause(ex);
      throw exToThrow;
    }
  }

  public static UrlResource from(URI uri) throws UncheckedIOException {
    try {
      return new UrlResource(uri);
    } catch (MalformedURLException var2) {
      MalformedURLException ex = var2;
      throw new UncheckedIOException(ex);
    }
  }

  public static UrlResource from(String path) throws UncheckedIOException {
    try {
      return new UrlResource(path);
    } catch (MalformedURLException var2) {
      MalformedURLException ex = var2;
      throw new UncheckedIOException(ex);
    }
  }

  private String getCleanedUrl() {
    String cleanedUrl = this.cleanedUrl;
    if (cleanedUrl != null) {
      return cleanedUrl;
    } else {
      String originalPath = (this.uri != null ? this.uri : this.url).toString();
      cleanedUrl = StringUtils.cleanPath(originalPath);
      this.cleanedUrl = cleanedUrl;
      return cleanedUrl;
    }
  }

  public InputStream getInputStream() throws IOException {
    URLConnection con = this.url.openConnection();
    this.customizeConnection(con);

    try {
      return con.getInputStream();
    } catch (IOException var4) {
      IOException ex = var4;
      if (con instanceof HttpURLConnection httpConn) {
        httpConn.disconnect();
      }

      throw ex;
    }
  }

  protected void customizeConnection(URLConnection con) throws IOException {
    super.customizeConnection(con);
    String userInfo = this.url.getUserInfo();
    if (userInfo != null) {
      String encodedCredentials = Base64.getUrlEncoder().encodeToString(userInfo.getBytes());
      con.setRequestProperty(AUTHORIZATION, "Basic " + encodedCredentials);
    }

  }

  public URL getURL() {
    return this.url;
  }

  public URI getURI() throws IOException {
    return this.uri != null ? this.uri : super.getURI();
  }

  public boolean isFile() {
    return this.uri != null ? super.isFile(this.uri) : super.isFile();
  }

  public File getFile() throws IOException {
    return this.uri != null ? super.getFile(this.uri) : super.getFile();
  }

  public Resource createRelative(String relativePath) throws MalformedURLException {
    return new UrlResource(this.createRelativeURL(relativePath));
  }

  protected URL createRelativeURL(String relativePath) throws MalformedURLException {
    if (relativePath.startsWith("/")) {
      relativePath = relativePath.substring(1);
    }

    return ResourceUtils.toRelativeURL(this.url, relativePath);
  }

  @Nullable
  public String getFilename() {
    String path;
    if (this.uri != null) {
      path = this.uri.getPath();
      if (path != null) {
        return StringUtils.getFilename(this.uri.getPath());
      }
    }

    path = StringUtils.getFilename(StringUtils.cleanPath(this.url.getPath()));
    return path != null ? URLDecoder.decode(path, StandardCharsets.UTF_8) : null;
  }

  public String getDescription() {
    Object var10000 = this.uri != null ? this.uri : this.url;
    return "URL [" + var10000 + "]";
  }

  public boolean equals(@Nullable Object other) {
    boolean var10000;
    if (this != other) {
      label26: {
        if (other instanceof UrlResource) {
          UrlResource that = (UrlResource)other;
          if (this.getCleanedUrl().equals(that.getCleanedUrl())) {
            break label26;
          }
        }

        var10000 = false;
        return var10000;
      }
    }

    var10000 = true;
    return var10000;
  }

  public int hashCode() {
    return this.getCleanedUrl().hashCode();
  }
}

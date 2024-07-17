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
import io.github.jelilio.i18nresourcebundle.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Supplier;

/**
 * Convenience base class for {@link Resource} implementations,
 * pre-implementing typical behavior.
 *
 * <p>The "exists" method will check whether a File or InputStream can
 * be opened; "isOpen" will always return false; "getURL" and "getFile"
 * throw an exception; and "toString" will return the description.
 *
 */
public abstract class AbstractResource implements Resource {
  public AbstractResource() {
  }

  public boolean exists() {
    if (this.isFile()) {
      try {
        return this.getFile().exists();
      } catch (IOException var3) {
        IOException ex = var3;
        this.debug(() -> {
          return "Could not retrieve File for existence check of " + this.getDescription();
        }, ex);
      }
    }

    try {
      this.getInputStream().close();
      return true;
    } catch (Throwable var2) {
      Throwable ex = var2;
      this.debug(() -> {
        return "Could not retrieve InputStream for existence check of " + this.getDescription();
      }, ex);
      return false;
    }
  }

  public boolean isReadable() {
    return this.exists();
  }

  public boolean isOpen() {
    return false;
  }

  public boolean isFile() {
    return false;
  }

  public URL getURL() throws IOException {
    throw new FileNotFoundException(this.getDescription() + " cannot be resolved to URL");
  }

  public URI getURI() throws IOException {
    URL url = this.getURL();

    try {
      return ResourceUtils.toURI(url);
    } catch (URISyntaxException var3) {
      URISyntaxException ex = var3;
      throw new IOException("Invalid URI [" + url + "]", ex);
    }
  }

  public File getFile() throws IOException {
    throw new FileNotFoundException(this.getDescription() + " cannot be resolved to absolute file path");
  }

  public ReadableByteChannel readableChannel() throws IOException {
    return Channels.newChannel(this.getInputStream());
  }

  public long contentLength() throws IOException {
    InputStream is = this.getInputStream();

    try {
      long size = 0L;

      int read;
      for(byte[] buf = new byte[256]; (read = is.read(buf)) != -1; size += (long)read) {
      }

      long var6 = size;
      return var6;
    } finally {
      try {
        is.close();
      } catch (IOException var14) {
        IOException ex = var14;
        this.debug(() -> {
          return "Could not close content-length InputStream for " + this.getDescription();
        }, ex);
      }

    }
  }

  public long lastModified() throws IOException {
    File fileToCheck = this.getFileForLastModifiedCheck();
    long lastModified = fileToCheck.lastModified();
    if (lastModified == 0L && !fileToCheck.exists()) {
      throw new FileNotFoundException(this.getDescription() + " cannot be resolved in the file system for checking its last-modified timestamp");
    } else {
      return lastModified;
    }
  }

  protected File getFileForLastModifiedCheck() throws IOException {
    return this.getFile();
  }

  public Resource createRelative(String relativePath) throws IOException {
    throw new FileNotFoundException("Cannot create a relative resource for " + this.getDescription());
  }

  @Nullable
  public String getFilename() {
    return null;
  }

  private void debug(Supplier<String> message, Throwable ex) {
//    Log logger = LogFactory.getLog(this.getClass());
//    if (logger.isDebugEnabled()) {
//      logger.debug(message.get(), ex);
//    }
  }

  public boolean equals(@Nullable Object other) {
    boolean var10000;
    if (this != other) {
      label26: {
        if (other instanceof Resource) {
          Resource that = (Resource)other;
          if (this.getDescription().equals(that.getDescription())) {
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
    return this.getDescription().hashCode();
  }

  public String toString() {
    return this.getDescription();
  }
}

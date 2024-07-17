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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * JBoss VFS based {@link Resource} implementation.
 *
 */
public class VfsResource extends AbstractResource {
  private final Object resource;

  public VfsResource(Object resource) {
    Assert.notNull(resource, "VirtualFile must not be null");
    this.resource = resource;
  }

  public InputStream getInputStream() throws IOException {
    return VfsUtils.getInputStream(this.resource);
  }

  public boolean exists() {
    return VfsUtils.exists(this.resource);
  }

  public boolean isReadable() {
    return VfsUtils.isReadable(this.resource);
  }

  public URL getURL() throws IOException {
    try {
      return VfsUtils.getURL(this.resource);
    } catch (Exception var2) {
      Exception ex = var2;
      throw new IOException("Failed to obtain URL for file " + this.resource, ex);
    }
  }

  public URI getURI() throws IOException {
    try {
      return VfsUtils.getURI(this.resource);
    } catch (Exception var2) {
      Exception ex = var2;
      throw new IOException("Failed to obtain URI for " + this.resource, ex);
    }
  }

  public File getFile() throws IOException {
    return VfsUtils.getFile(this.resource);
  }

  public long contentLength() throws IOException {
    return VfsUtils.getSize(this.resource);
  }

  public long lastModified() throws IOException {
    return VfsUtils.getLastModified(this.resource);
  }

  public Resource createRelative(String relativePath) throws IOException {
    if (!relativePath.startsWith(".") && relativePath.contains("/")) {
      try {
        return new VfsResource(VfsUtils.getChild(this.resource, relativePath));
      } catch (IOException var3) {
      }
    }

    return new VfsResource(VfsUtils.getRelative(ResourceUtils.toRelativeURL(this.getURL(), relativePath)));
  }

  public String getFilename() {
    return VfsUtils.getName(this.resource);
  }

  public String getDescription() {
    return "VFS resource [" + this.resource + "]";
  }

  public boolean equals(@Nullable Object other) {
    boolean var10000;
    if (this != other) {
      label26: {
        if (other instanceof VfsResource) {
          VfsResource that = (VfsResource)other;
          if (this.resource.equals(that.resource)) {
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
    return this.resource.hashCode();
  }
}

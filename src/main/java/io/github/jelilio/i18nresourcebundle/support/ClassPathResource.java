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
import io.github.jelilio.i18nresourcebundle.util.ClassUtils;
import io.github.jelilio.i18nresourcebundle.util.ObjectUtils;
import io.github.jelilio.i18nresourcebundle.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * {@link Resource} implementation for class path resources. Uses either a
 * given {@link ClassLoader} or a given {@link Class} for loading resources.
 *
 * <p>Supports resolution as {@code java.io.File} if the class path
 * resource resides in the file system, but not for resources in a JAR.
 * Always supports resolution as {@code java.net.URL}.
 *
 * @see ClassLoader#getResourceAsStream(String)
 * @see ClassLoader#getResource(String)
 * @see Class#getResourceAsStream(String)
 * @see Class#getResource(String)
 */

public class ClassPathResource extends AbstractFileResolvingResource {
  private final String path;
  private final String absolutePath;
  @Nullable
  private final ClassLoader classLoader;
  @Nullable
  private final Class<?> clazz;

  public ClassPathResource(String path) {
    this(path, (ClassLoader)null);
  }

  public ClassPathResource(String path, @Nullable ClassLoader classLoader) {
    Assert.notNull(path, "Path must not be null");
    String pathToUse = StringUtils.cleanPath(path);
    if (pathToUse.startsWith("/")) {
      pathToUse = pathToUse.substring(1);
    }

    this.path = pathToUse;
    this.absolutePath = pathToUse;
    this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
    this.clazz = null;
  }

  public ClassPathResource(String path, @Nullable Class<?> clazz) {
    Assert.notNull(path, "Path must not be null");
    this.path = StringUtils.cleanPath(path);
    String absolutePath = this.path;
    if (clazz != null && !absolutePath.startsWith("/")) {
      String var10000 = ClassUtils.classPackageAsResourcePath(clazz);
      absolutePath = var10000 + "/" + absolutePath;
    } else if (absolutePath.startsWith("/")) {
      absolutePath = absolutePath.substring(1);
    }

    this.absolutePath = absolutePath;
    this.classLoader = null;
    this.clazz = clazz;
  }

  public final String getPath() {
    return this.absolutePath;
  }

  @Nullable
  public final ClassLoader getClassLoader() {
    return this.clazz != null ? this.clazz.getClassLoader() : this.classLoader;
  }

  public boolean exists() {
    return this.resolveURL() != null;
  }

  public boolean isReadable() {
    URL url = this.resolveURL();
    return url != null && this.checkReadable(url);
  }

  @Nullable
  protected URL resolveURL() {
    try {
      if (this.clazz != null) {
        return this.clazz.getResource(this.path);
      } else {
        return this.classLoader != null ? this.classLoader.getResource(this.absolutePath) : ClassLoader.getSystemResource(this.absolutePath);
      }
    } catch (IllegalArgumentException var2) {
      return null;
    }
  }

  public InputStream getInputStream() throws IOException {
    InputStream is;
    if (this.clazz != null) {
      is = this.clazz.getResourceAsStream(this.path);
    } else if (this.classLoader != null) {
      is = this.classLoader.getResourceAsStream(this.absolutePath);
    } else {
      is = ClassLoader.getSystemResourceAsStream(this.absolutePath);
    }

    if (is == null) {
      throw new FileNotFoundException(this.getDescription() + " cannot be opened because it does not exist");
    } else {
      return is;
    }
  }

  public URL getURL() throws IOException {
    URL url = this.resolveURL();
    if (url == null) {
      throw new FileNotFoundException(this.getDescription() + " cannot be resolved to URL because it does not exist");
    } else {
      return url;
    }
  }

  public Resource createRelative(String relativePath) {
    String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
    return this.clazz != null ? new ClassPathResource(pathToUse, this.clazz) : new ClassPathResource(pathToUse, this.classLoader);
  }

  @Nullable
  public String getFilename() {
    return StringUtils.getFilename(this.absolutePath);
  }

  public String getDescription() {
    return "class path resource [" + this.absolutePath + "]";
  }

  public boolean equals(@Nullable Object other) {
    boolean var10000;
    if (this != other) {
      label28: {
        if (other instanceof ClassPathResource) {
          ClassPathResource that = (ClassPathResource)other;
          if (this.absolutePath.equals(that.absolutePath) && ObjectUtils.nullSafeEquals(this.getClassLoader(), that.getClassLoader())) {
            break label28;
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
    return this.absolutePath.hashCode();
  }
}


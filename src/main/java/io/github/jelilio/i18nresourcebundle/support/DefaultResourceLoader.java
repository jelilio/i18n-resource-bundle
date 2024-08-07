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

import io.github.jelilio.i18nresourcebundle.ContextResource;
import io.github.jelilio.i18nresourcebundle.ProtocolResolver;
import io.github.jelilio.i18nresourcebundle.Resource;
import io.github.jelilio.i18nresourcebundle.ResourceLoader;
import io.github.jelilio.i18nresourcebundle.annotation.Nullable;
import io.github.jelilio.i18nresourcebundle.util.Assert;
import io.github.jelilio.i18nresourcebundle.util.ClassUtils;
import io.github.jelilio.i18nresourcebundle.util.ResourceUtils;
import io.github.jelilio.i18nresourcebundle.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the {@link ResourceLoader} interface.
 *
 *
 * <p>Will return a {@link UrlResource} if the location value is a URL,
 * and a {@link ClassPathResource} if it is a non-URL path or a
 * "classpath:" pseudo-URL.
 *
 */
public class DefaultResourceLoader implements ResourceLoader {
  @Nullable
  private ClassLoader classLoader;

  private final Set<ProtocolResolver> protocolResolvers = new LinkedHashSet<>(4);

  private final Map<Class<?>, Map<Resource, ?>> resourceCaches = new ConcurrentHashMap<>(4);


  /**
   * Create a new DefaultResourceLoader.
   * <p>ClassLoader access will happen using the thread context class loader
   * at the time of actual resource access (since 5.3). For more control, pass
   * a specific ClassLoader to {@link #DefaultResourceLoader(ClassLoader)}.
   * @see java.lang.Thread#getContextClassLoader()
   */
  public DefaultResourceLoader() {
  }

  public DefaultResourceLoader(@Nullable ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public void setClassLoader(@Nullable ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  @Nullable
  public ClassLoader getClassLoader() {
    return this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader();
  }

  public void addProtocolResolver(ProtocolResolver resolver) {
    Assert.notNull(resolver, "ProtocolResolver must not be null");
    this.protocolResolvers.add(resolver);
  }

  public Collection<ProtocolResolver> getProtocolResolvers() {
    return this.protocolResolvers;
  }

  /**
   * Obtain a cache for the given value type, keyed by {@link Resource}.
   * @param valueType the value type, e.g. an ASM {@code MetadataReader}
   * @return the cache {@link Map}, shared at the {@code ResourceLoader} level
   * @since 5.0
   */
  @SuppressWarnings("unchecked")
  public <T> Map<Resource, T> getResourceCache(Class<T> valueType) {
    return (Map<Resource, T>) this.resourceCaches.computeIfAbsent(valueType, key -> new ConcurrentHashMap<>());
  }

  public void clearResourceCaches() {
    this.resourceCaches.clear();
  }

  @Override
  public Resource getResource(String location) {
    Assert.notNull(location, "Location must not be null");

    for (ProtocolResolver protocolResolver : getProtocolResolvers()) {
      Resource resource = protocolResolver.resolve(location, this);
      if (resource != null) {
        return resource;
      }
    }

    if (location.startsWith("/")) {
      return getResourceByPath(location);
    }
    else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
      return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
    }
    else {
      try {
        // Try to parse the location as a URL...
        URL url = ResourceUtils.toURL(location);
        return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
      }
      catch (MalformedURLException ex) {
        // No URL -> resolve as resource path.
        return getResourceByPath(location);
      }
    }
  }

  protected Resource getResourceByPath(String path) {
    return new ClassPathContextResource(path, this.getClassLoader());
  }

  protected static class ClassPathContextResource extends ClassPathResource implements ContextResource {
    public ClassPathContextResource(String path, @Nullable ClassLoader classLoader) {
      super(path, classLoader);
    }

    public String getPathWithinContext() {
      return this.getPath();
    }

    public Resource createRelative(String relativePath) {
      String pathToUse = StringUtils.applyRelativePath(this.getPath(), relativePath);
      return new ClassPathContextResource(pathToUse, this.getClassLoader());
    }
  }
}


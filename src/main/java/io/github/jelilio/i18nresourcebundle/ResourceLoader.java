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

package io.github.jelilio.i18nresourcebundle;

import io.github.jelilio.i18nresourcebundle.annotation.Nullable;

/**
 * Strategy interface for loading resources (e.g., class path or file system
 * resources).
 *
 * <p>Bean properties of type {@code Resource} and {@code Resource[]} can be populated
 * from Strings when running in an ApplicationContext, using the particular
 * context's resource loading strategy.
 *
 * @see Resource
 * @see io.github.jelilio.i18nresourcebundle.ResourceLoaderAware
 */
public interface ResourceLoader {
  String CLASSPATH_URL_PREFIX = "classpath:";

  Resource getResource(String location);

  @Nullable
  ClassLoader getClassLoader();
}

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
 * A resolution strategy for protocol-specific resource handles.
 *
 * <p>Used as an SPI for {@link io.github.jelilio.i18nresourcebundle.support.DefaultResourceLoader}, allowing for
 * custom protocols to be handled without subclassing the loader
 * implementation (or application context implementation).
 *
 * @see io.github.jelilio.i18nresourcebundle.support.DefaultResourceLoader#addProtocolResolver
 */

@FunctionalInterface
public interface ProtocolResolver {
  @Nullable
  Resource resolve(String location, ResourceLoader resourceLoader);
}
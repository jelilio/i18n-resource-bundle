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

/**
 * Extended interface for a resource that is loaded from an enclosing
 * 'context', e.g. from plain classpath paths or relative file system paths
 * (specified without an explicit prefix, hence applying relative to the local
 * {@link ResourceLoader}'s context).
 *
 */
public interface ContextResource extends Resource {
  String getPathWithinContext();
}


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

import io.github.jelilio.i18nresourcebundle.annotation.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Miscellaneous collection utility methods.
 *
 */
public abstract class CollectionUtils {

  public CollectionUtils() {
  }

  public static boolean isEmpty(@Nullable Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  public static boolean isEmpty(@Nullable Map<?, ?> map) {
    return map == null || map.isEmpty();
  }
}

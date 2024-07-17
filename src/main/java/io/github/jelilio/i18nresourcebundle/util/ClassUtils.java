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

/**
 * Miscellaneous {@code java.lang.Class} utility methods.
 *
 * @see ReflectionUtils
 */
public abstract class ClassUtils {
  /** The package separator character: {@code '.'}. */
  private static final char PACKAGE_SEPARATOR = '.';
  /** The path separator character: {@code '/'}. */
  private static final char PATH_SEPARATOR = '/';


  @Nullable
  public static ClassLoader getDefaultClassLoader() {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    }
    catch (Throwable ex) {
      // Cannot access thread context ClassLoader - falling back...
    }
    if (cl == null) {
      // No thread context class loader -> use class loader of this class.
      cl = ClassUtils.class.getClassLoader();
      if (cl == null) {
        // getClassLoader() returning null indicates the bootstrap ClassLoader
        try {
          cl = ClassLoader.getSystemClassLoader();
        }
        catch (Throwable ex) {
          // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
        }
      }
    }
    return cl;
  }

  public static String classPackageAsResourcePath(@Nullable Class<?> clazz) {
    if (clazz == null) {
      return "";
    }
    String className = clazz.getName();
    int packageEndIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
    if (packageEndIndex == -1) {
      return "";
    }
    String packageName = className.substring(0, packageEndIndex);
    return packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
  }
}

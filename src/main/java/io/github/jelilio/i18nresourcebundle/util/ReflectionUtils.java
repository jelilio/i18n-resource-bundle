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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * Simple utility class for working with the reflection API and handling
 * reflection exceptions.
 *
 * <p>Only intended for internal use.
 *
 */
public abstract class ReflectionUtils {
  /** URL protocol for a general JBoss VFS resource: "vfs". */
  public static final String URL_PROTOCOL_VFS = "vfs";
  private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

  public static void handleInvocationTargetException(InvocationTargetException ex) {
    rethrowRuntimeException(ex.getTargetException());
  }

  public static void rethrowRuntimeException(@Nullable Throwable ex) {
    if (ex instanceof RuntimeException runtimeException) {
      throw runtimeException;
    }
    if (ex instanceof Error error) {
      throw error;
    }
    throw new UndeclaredThrowableException(ex);
  }

  @Nullable
  public static Object invokeMethod(Method method, @Nullable Object target) {
    return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
  }

  @Nullable
  public static Object invokeMethod(Method method, @Nullable Object target, @Nullable Object... args) {
    try {
      return method.invoke(target, args);
    }
    catch (Exception ex) {
      handleReflectionException(ex);
    }
    throw new IllegalStateException("Should never get here");
  }

  @Nullable
  public static Object getField(Field field, @Nullable Object target) {
    try {
      return field.get(target);
    }
    catch (IllegalAccessException ex) {
      handleReflectionException(ex);
    }
    throw new IllegalStateException("Should never get here");
  }

  public static void handleReflectionException(Exception ex) {
    if (ex instanceof NoSuchMethodException) {
      throw new IllegalStateException("Method not found: " + ex.getMessage());
    }
    if (ex instanceof IllegalAccessException) {
      throw new IllegalStateException("Could not access method or field: " + ex.getMessage());
    }
    if (ex instanceof InvocationTargetException invocationTargetException) {
      handleInvocationTargetException(invocationTargetException);
    }
    if (ex instanceof RuntimeException runtimeException) {
      throw runtimeException;
    }
    throw new UndeclaredThrowableException(ex);
  }
}

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

  /**
   * Rethrow the given {@link Throwable exception}, which is presumably the
   * <em>target exception</em> of an {@link InvocationTargetException}.
   * Should only be called if no checked exception is expected to be thrown
   * by the target method.
   * <p>Rethrows the underlying exception cast to a {@link RuntimeException} or
   * {@link Error} if appropriate; otherwise, throws an
   * {@link UndeclaredThrowableException}.
   * @param ex the exception to rethrow
   * @throws RuntimeException the rethrown exception
   */
  public static void rethrowRuntimeException(@Nullable Throwable ex) {
    if (ex instanceof RuntimeException runtimeException) {
      throw runtimeException;
    }
    if (ex instanceof Error error) {
      throw error;
    }
    throw new UndeclaredThrowableException(ex);
  }

  /**
   * Invoke the specified {@link Method} against the supplied target object with no arguments.
   * The target object can be {@code null} when invoking a static {@link Method}.
   * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
   * @param method the method to invoke
   * @param target the target object to invoke the method on
   * @return the invocation result, if any
   * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
   */
  @Nullable
  public static Object invokeMethod(Method method, @Nullable Object target) {
    return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
  }

  /**
   * Invoke the specified {@link Method} against the supplied target object with the
   * supplied arguments. The target object can be {@code null} when invoking a
   * static {@link Method}.
   * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
   * @param method the method to invoke
   * @param target the target object to invoke the method on
   * @param args the invocation arguments (may be {@code null})
   * @return the invocation result, if any
   */
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

  /**
   * Get the field represented by the supplied {@link Field field object} on the
   * specified {@link Object target object}. In accordance with {@link Field#get(Object)}
   * semantics, the returned value is automatically wrapped if the underlying field
   * has a primitive type.
   * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException(Exception)}.
   * @param field the field to get
   * @param target the target object from which to get the field
   * (or {@code null} for a static field)
   * @return the field's current value
   */
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

  /**
   * Handle the given reflection exception.
   * <p>Should only be called if no checked exception is expected to be thrown
   * by a target method, or if an error occurs while accessing a method or field.
   * <p>Throws the underlying RuntimeException or Error in case of an
   * InvocationTargetException with such a root cause. Throws an
   * IllegalStateException with an appropriate message or
   * UndeclaredThrowableException otherwise.
   * @param ex the reflection exception to handle
   */
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

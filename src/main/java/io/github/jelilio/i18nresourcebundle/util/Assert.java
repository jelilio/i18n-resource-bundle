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
import java.util.function.Supplier;

/**
 * Assertion utility class that assists in validating arguments.
 *
 * <p>Useful for identifying programmer errors early and clearly at runtime.
 *
 * <p>For example, if the contract of a public method states it does not
 * allow {@code null} arguments, {@code Assert} can be used to validate that
 * contract. Doing this clearly indicates a contract violation when it
 * occurs and protects the class's invariants.
 *
 * <p>Typically used to validate method arguments rather than configuration
 * properties, to check for cases that are usually programmer errors rather
 * than configuration errors. In contrast to configuration initialization
 * code, there is usually no point in falling back to defaults in such methods.
 *
 * <p>This class is similar to JUnit's assertion library. If an argument value is
 * deemed invalid, an {@link IllegalArgumentException} is thrown (typically).
 * For example:
 *
 * <pre class="code">
 * Assert.notNull(clazz, "The class must not be null");
 * Assert.isTrue(i &gt; 0, "The value must be greater than zero");</pre>
 *
 * <p>Mainly for internal use within the framework; for a more comprehensive suite
 * of assertion utilities consider {@code org.apache.commons.lang3.Validate} from
 * <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a>,
 * Google Guava's
 * <a href="https://github.com/google/guava/wiki/PreconditionsExplained">Preconditions</a>,
 * or similar third-party libraries.
 */
public abstract class Assert {
  public Assert() {
  }

  public static void state(boolean expression, String message) {
    if (!expression) {
      throw new IllegalStateException(message);
    }
  }

  public static void state(boolean expression, Supplier<String> messageSupplier) {
    if (!expression) {
      throw new IllegalStateException(nullSafeGet(messageSupplier));
    }
  }

  public static void isTrue(boolean expression, String message) {
    if (!expression) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
    if (!expression) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void isNull(@Nullable Object object, String message) {
    if (object != null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void isNull(@Nullable Object object, Supplier<String> messageSupplier) {
    if (object != null) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void notNull(@Nullable Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notNull(@Nullable Object object, Supplier<String> messageSupplier) {
    if (object == null) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void hasLength(@Nullable String text, String message) {
    if (!StringUtils.hasLength(text)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void hasLength(@Nullable String text, Supplier<String> messageSupplier) {
    if (!StringUtils.hasLength(text)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void hasText(@Nullable String text, String message) {
    if (!StringUtils.hasText(text)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void hasText(@Nullable String text, Supplier<String> messageSupplier) {
    if (!StringUtils.hasText(text)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void doesNotContain(@Nullable String textToSearch, String substring, String message) {
    if (StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring) && textToSearch.contains(substring)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void doesNotContain(@Nullable String textToSearch, String substring, Supplier<String> messageSupplier) {
    if (StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring) && textToSearch.contains(substring)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void notEmpty(@Nullable Object[] array, String message) {
    if (ObjectUtils.isEmpty(array)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEmpty(@Nullable Object[] array, Supplier<String> messageSupplier) {
    if (ObjectUtils.isEmpty(array)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  /**
   * Assert that an array contains no {@code null} elements.
   * <p>Note: Does not complain if the array is empty!
   * <pre class="code">Assert.noNullElements(array, "The array must contain non-null elements");</pre>
   * @param array the array to check
   * @param message the exception message to use if the assertion fails
   * @throws IllegalArgumentException if the object array contains a {@code null} element
   */
  public static void noNullElements(@Nullable Object[] array, String message) {
    if (array != null) {
      for (Object element : array) {
        if (element == null) {
          throw new IllegalArgumentException(message);
        }
      }
    }
  }

  /**
   * Assert that an array contains no {@code null} elements.
   * <p>Note: Does not complain if the array is empty!
   * <pre class="code">
   * Assert.noNullElements(array, () -&gt; "The " + arrayType + " array must contain non-null elements");
   * </pre>
   * @param array the array to check
   * @param messageSupplier a supplier for the exception message to use if the
   * assertion fails
   * @throws IllegalArgumentException if the object array contains a {@code null} element
   * @since 5.0
   */
  public static void noNullElements(@Nullable Object[] array, Supplier<String> messageSupplier) {
    if (array != null) {
      for (Object element : array) {
        if (element == null) {
          throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
      }
    }
  }

  /**
   * Assert that a collection contains no {@code null} elements.
   * <p>Note: Does not complain if the collection is empty!
   * <pre class="code">Assert.noNullElements(collection, "Collection must contain non-null elements");</pre>
   * @param collection the collection to check
   * @param message the exception message to use if the assertion fails
   * @throws IllegalArgumentException if the collection contains a {@code null} element
   * @since 5.2
   */
  public static void noNullElements(@Nullable Collection<?> collection, String message) {
    if (collection != null) {
      for (Object element : collection) {
        if (element == null) {
          throw new IllegalArgumentException(message);
        }
      }
    }
  }

  /**
   * Assert that a collection contains no {@code null} elements.
   * <p>Note: Does not complain if the collection is empty!
   * <pre class="code">
   * Assert.noNullElements(collection, () -&gt; "Collection " + collectionName + " must contain non-null elements");
   * </pre>
   * @param collection the collection to check
   * @param messageSupplier a supplier for the exception message to use if the
   * assertion fails
   * @throws IllegalArgumentException if the collection contains a {@code null} element
   * @since 5.2
   */
  public static void noNullElements(@Nullable Collection<?> collection, Supplier<String> messageSupplier) {
    if (collection != null) {
      for (Object element : collection) {
        if (element == null) {
          throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
      }
    }
  }

  /**
   * Assert that a collection contains elements; that is, it must not be
   * {@code null} and must contain at least one element.
   * <pre class="code">Assert.notEmpty(collection, "Collection must contain elements");</pre>
   * @param collection the collection to check
   * @param message the exception message to use if the assertion fails
   * @throws IllegalArgumentException if the collection is {@code null} or
   * contains no elements
   */
  public static void notEmpty(@Nullable Collection<?> collection, String message) {
    if (CollectionUtils.isEmpty(collection)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEmpty(@Nullable Collection<?> collection, Supplier<String> messageSupplier) {
    if (CollectionUtils.isEmpty(collection)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void notEmpty(@Nullable Map<?, ?> map, String message) {
    if (CollectionUtils.isEmpty(map)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEmpty(@Nullable Map<?, ?> map, Supplier<String> messageSupplier) {
    if (CollectionUtils.isEmpty(map)) {
      throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
  }

  public static void isInstanceOf(Class<?> type, @Nullable Object obj, String message) {
    notNull(type, (String)"Type to check against must not be null");
    if (!type.isInstance(obj)) {
      instanceCheckFailed(type, obj, message);
    }

  }

  public static void isInstanceOf(Class<?> type, @Nullable Object obj, Supplier<String> messageSupplier) {
    notNull(type, (String)"Type to check against must not be null");
    if (!type.isInstance(obj)) {
      instanceCheckFailed(type, obj, nullSafeGet(messageSupplier));
    }

  }

  public static void isInstanceOf(Class<?> type, @Nullable Object obj) {
    isInstanceOf(type, obj, "");
  }

  public static void isAssignable(Class<?> superType, @Nullable Class<?> subType, String message) {
    notNull(superType, (String)"Supertype to check against must not be null");
    if (subType == null || !superType.isAssignableFrom(subType)) {
      assignableCheckFailed(superType, subType, message);
    }

  }

  public static void isAssignable(Class<?> superType, @Nullable Class<?> subType, Supplier<String> messageSupplier) {
    notNull(superType, (String)"Supertype to check against must not be null");
    if (subType == null || !superType.isAssignableFrom(subType)) {
      assignableCheckFailed(superType, subType, nullSafeGet(messageSupplier));
    }

  }

  public static void isAssignable(Class<?> superType, Class<?> subType) {
    isAssignable(superType, subType, "");
  }

  private static void instanceCheckFailed(Class<?> type, @Nullable Object obj, @Nullable String msg) {
    String className = obj != null ? obj.getClass().getName() : "null";
    String result = "";
    boolean defaultMessage = true;
    if (StringUtils.hasLength(msg)) {
      if (endsWithSeparator(msg)) {
        result = msg + " ";
      } else {
        result = messageWithTypeName(msg, className);
        defaultMessage = false;
      }
    }

    if (defaultMessage) {
      result = result + "Object of class [" + className + "] must be an instance of " + type;
    }

    throw new IllegalArgumentException(result);
  }

  private static void assignableCheckFailed(Class<?> superType, @Nullable Class<?> subType, @Nullable String msg) {
    String result = "";
    boolean defaultMessage = true;
    if (StringUtils.hasLength(msg)) {
      if (endsWithSeparator(msg)) {
        result = msg + " ";
      } else {
        result = messageWithTypeName(msg, subType);
        defaultMessage = false;
      }
    }

    if (defaultMessage) {
      result = result + subType + " is not assignable to " + superType;
    }

    throw new IllegalArgumentException(result);
  }

  private static boolean endsWithSeparator(String msg) {
    return msg.endsWith(":") || msg.endsWith(";") || msg.endsWith(",") || msg.endsWith(".");
  }

  private static String messageWithTypeName(String msg, @Nullable Object typeName) {
    return msg + (msg.endsWith(" ") ? "" : ": ") + typeName;
  }

  @Nullable
  private static String nullSafeGet(@Nullable Supplier<String> messageSupplier) {
    return messageSupplier != null ? (String)messageSupplier.get() : null;
  }
}


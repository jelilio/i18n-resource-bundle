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

import java.lang.reflect.Array;
import java.util.*;

/**
 * Miscellaneous object utility methods.
 *
 * <p>Mainly for internal use within the framework.
 *
 */

public abstract class ObjectUtils {
  private static final String NULL_STRING = "null";
  private static final String EMPTY_STRING = "";

  /**
   * Determine whether the given array is empty:
   * i.e. {@code null} or of zero length.
   * @param array the array to check
   * @see #isEmpty(Object)
   */
  public static boolean isEmpty(@Nullable Object[] array) {
    return (array == null || array.length == 0);
  }

  /**
   * Determine whether the given object is empty.
   * <p>This method supports the following object types.
   * <ul>
   * <li>{@code Optional}: considered empty if not {@link Optional#isPresent()}</li>
   * <li>{@code Array}: considered empty if its length is zero</li>
   * <li>{@link CharSequence}: considered empty if its length is zero</li>
   * <li>{@link Collection}: delegates to {@link Collection#isEmpty()}</li>
   * <li>{@link Map}: delegates to {@link Map#isEmpty()}</li>
   * </ul>
   * <p>If the given object is non-null and not one of the aforementioned
   * supported types, this method returns {@code false}.
   * @param obj the object to check
   * @return {@code true} if the object is {@code null} or <em>empty</em>
   * @since 4.2
   * @see Optional#isPresent()
   * @see ObjectUtils#isEmpty(Object[])
   */
  public static boolean isEmpty(@Nullable Object obj) {
    if (obj == null) {
      return true;
    }

    if (obj instanceof Optional<?> optional) {
      return optional.isEmpty();
    }
    if (obj instanceof CharSequence charSequence) {
      return charSequence.isEmpty();
    }
    if (obj.getClass().isArray()) {
      return Array.getLength(obj) == 0;
    }
    if (obj instanceof Collection<?> collection) {
      return collection.isEmpty();
    }
    if (obj instanceof Map<?, ?> map) {
      return map.isEmpty();
    }

    // else
    return false;
  }

  /**
   * Determine if the given objects are equal, returning {@code true} if
   * both are {@code null} or {@code false} if only one is {@code null}.
   * <p>Compares arrays with {@code Arrays.equals}, performing an equality
   * check based on the array elements rather than the array reference.
   * @param o1 first Object to compare
   * @param o2 second Object to compare
   * @return whether the given objects are equal
   * @see Object#equals(Object)
   * @see java.util.Arrays#equals
   */
  public static boolean nullSafeEquals(@Nullable Object o1, @Nullable Object o2) {
    if (o1 == o2) {
      return true;
    }
    if (o1 == null || o2 == null) {
      return false;
    }
    if (o1.equals(o2)) {
      return true;
    }
    if (o1.getClass().isArray() && o2.getClass().isArray()) {
      return arrayEquals(o1, o2);
    }
    return false;
  }

  /**
   * Compare the given arrays with {@code Arrays.equals}, performing an equality
   * check based on the array elements rather than the array reference.
   * @param o1 first array to compare
   * @param o2 second array to compare
   * @return whether the given objects are equal
   * @see #nullSafeEquals(Object, Object)
   * @see java.util.Arrays#equals
   */
  private static boolean arrayEquals(Object o1, Object o2) {
    if (o1 instanceof Object[] objects1) {
      if (o2 instanceof Object[] objects2) {
        return Arrays.equals(objects1, objects2);
      }
    }

    if (o1 instanceof boolean[] booleans1) {
      if (o2 instanceof boolean[] booleans2) {
        return Arrays.equals(booleans1, booleans2);
      }
    }

    if (o1 instanceof byte[] bytes1) {
      if (o2 instanceof byte[] bytes2) {
        return Arrays.equals(bytes1, bytes2);
      }
    }

    if (o1 instanceof char[] chars1) {
      if (o2 instanceof char[] chars2) {
        return Arrays.equals(chars1, chars2);
      }
    }

    if (o1 instanceof double[] doubles1) {
      if (o2 instanceof double[] doubles2) {
        return Arrays.equals(doubles1, doubles2);
      }
    }

    if (o1 instanceof float[] floats1) {
      if (o2 instanceof float[] floats2) {
        return Arrays.equals(floats1, floats2);
      }
    }

    if (o1 instanceof int[] ints1) {
      if (o2 instanceof int[] ints2) {
        return Arrays.equals(ints1, ints2);
      }
    }

    if (o1 instanceof long[] longs1) {
      if (o2 instanceof long[] longs2) {
        return Arrays.equals(longs1, longs2);
      }
    }

    if (o1 instanceof short[] shorts1) {
      if (o2 instanceof short[] shorts2) {
        return Arrays.equals(shorts1, shorts2);
      }
    }

    return false;
  }

  /**
   * Return a hash code for the given elements, delegating to
   * {@link #nullSafeHashCode(Object)} for each element. Contrary
   * to {@link Objects#hash(Object...)}, this method can handle an
   * element that is an array.
   * @param elements the elements to be hashed
   * @return a hash value of the elements
   * @since 6.1
   */
  public static int nullSafeHash(@Nullable Object... elements) {
    if (elements == null) {
      return 0;
    }
    int result = 1;
    for (Object element : elements) {
      result = 31 * result + nullSafeHashCode(element);
    }
    return result;
  }

  /**
   * Return a hash code for the given object; typically the value of
   * {@code Object#hashCode()}}. If the object is an array,
   * this method will delegate to any of the {@code Arrays.hashCode}
   * methods. If the object is {@code null}, this method returns 0.
   * @see Object#hashCode()
   * @see Arrays
   */
  public static int nullSafeHashCode(@Nullable Object obj) {
    if (obj == null) {
      return 0;
    }
    if (obj.getClass().isArray()) {
      if (obj instanceof Object[] objects) {
        return Arrays.hashCode(objects);
      }
      if (obj instanceof boolean[] booleans) {
        return Arrays.hashCode(booleans);
      }
      if (obj instanceof byte[] bytes) {
        return Arrays.hashCode(bytes);
      }
      if (obj instanceof char[] chars) {
        return Arrays.hashCode(chars);
      }
      if (obj instanceof double[] doubles) {
        return Arrays.hashCode(doubles);
      }
      if (obj instanceof float[] floats) {
        return Arrays.hashCode(floats);
      }
      if (obj instanceof int[] ints) {
        return Arrays.hashCode(ints);
      }
      if (obj instanceof long[] longs) {
        return Arrays.hashCode(longs);
      }
      if (obj instanceof short[] shorts) {
        return Arrays.hashCode(shorts);
      }
    }
    return obj.hashCode();
  }


  /**
   * Return a String representation of the specified Object.
   * <p>Builds a String representation of the contents in case of an array.
   * Returns a {@code "null"} String if {@code obj} is {@code null}.
   * @param obj the object to build a String representation for
   * @return a String representation of {@code obj}
   */
  public static String nullSafeToString(@Nullable Object obj) {
    if (obj == null) {
      return NULL_STRING;
    }
    if (obj instanceof String string) {
      return string;
    }
    if (obj instanceof Object[] objects) {
      return nullSafeToString(objects);
    }
    if (obj instanceof boolean[] booleans) {
      return nullSafeToString(booleans);
    }
    if (obj instanceof byte[] bytes) {
      return nullSafeToString(bytes);
    }
    if (obj instanceof char[] chars) {
      return nullSafeToString(chars);
    }
    if (obj instanceof double[] doubles) {
      return nullSafeToString(doubles);
    }
    if (obj instanceof float[] floats) {
      return nullSafeToString(floats);
    }
    if (obj instanceof int[] ints) {
      return nullSafeToString(ints);
    }
    if (obj instanceof long[] longs) {
      return nullSafeToString(longs);
    }
    if (obj instanceof short[] shorts) {
      return nullSafeToString(shorts);
    }
    String str = obj.toString();
    return (str != null ? str : EMPTY_STRING);
  }

}

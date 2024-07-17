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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.StringTokenizer;

/**
 * Miscellaneous {@link String} utility methods.
 *
 * <p>Mainly for internal use within the framework; consider
 * <a href="https://commons.apache.org/proper/commons-lang/">Apache's Commons Lang</a>
 * for a more comprehensive suite of {@code String} utilities.
 *
 * <p>This class delivers some simple functionality that should really be
 * provided by the core Java {@link String} and {@link StringBuilder}
 * classes. It also provides easy-to-use methods to convert between
 * delimited strings, such as CSV strings, and collections and arrays.
 *
 */
public class StringUtils {
  private static final String[] EMPTY_STRING_ARRAY = {};

  private static final String FOLDER_SEPARATOR = "/";

  private static final char FOLDER_SEPARATOR_CHAR = '/';

  private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

  private static final String DOUBLE_BACKSLASHES = "\\\\";

  private static final String TOP_PATH = "..";

  private static final String CURRENT_PATH = ".";

  private static final char EXTENSION_SEPARATOR = '.';

  private static final int DEFAULT_TRUNCATION_THRESHOLD = 100;

  private static final String TRUNCATION_SUFFIX = " (truncated)...";

  /**
   * Convert a {@code String} array into a delimited {@code String} (e.g. CSV).
   * <p>Useful for {@code toString()} implementations.
   * @param arr the array to display (potentially {@code null} or empty)
   * @param delim the delimiter to use (typically a ",")
   * @return the delimited {@code String}
   */
  public static String arrayToDelimitedString(@Nullable Object[] arr, String delim) {
    if (ObjectUtils.isEmpty(arr)) {
      return "";
    }
    if (arr.length == 1) {
      return ObjectUtils.nullSafeToString(arr[0]);
    }

    StringJoiner sj = new StringJoiner(delim);
    for (Object elem : arr) {
      sj.add(String.valueOf(elem));
    }
    return sj.toString();
  }

  /**
   * Check whether the given {@code String} contains actual <em>text</em>.
   * <p>More specifically, this method returns {@code true} if the
   * {@code String} is not {@code null}, its length is greater than 0,
   * and it contains at least one non-whitespace character.
   * @param str the {@code String} to check (may be {@code null})
   * @return {@code true} if the {@code String} is not {@code null}, its
   * length is greater than 0, and it does not contain whitespace only
   * @see #hasText(CharSequence)
   * @see #hasLength(String)
   * @see Character#isWhitespace
   */
  public static boolean hasText(@Nullable String str) {
    return (str != null && !str.isBlank());
  }

  /**
   * Check whether the given {@code CharSequence} contains actual <em>text</em>.
   * <p>More specifically, this method returns {@code true} if the
   * {@code CharSequence} is not {@code null}, its length is greater than
   * 0, and it contains at least one non-whitespace character.
   * <p><pre class="code">
   * StringUtils.hasText(null) = false
   * StringUtils.hasText("") = false
   * StringUtils.hasText(" ") = false
   * StringUtils.hasText("12345") = true
   * StringUtils.hasText(" 12345 ") = true
   * </pre>
   * @param str the {@code CharSequence} to check (may be {@code null})
   * @return {@code true} if the {@code CharSequence} is not {@code null},
   * its length is greater than 0, and it does not contain whitespace only
   * @see #hasText(String)
   * @see Character#isWhitespace
   */
  public static boolean hasText(@Nullable CharSequence str) {
    if (str == null) {
      return false;
    }

    int strLen = str.length();
    if (strLen == 0) {
      return false;
    }

    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasLength(@Nullable String str) {
    return str != null && !str.isEmpty();
  }

  public static String[] toStringArray(@Nullable Collection<String> collection) {
    return !CollectionUtils.isEmpty(collection) ? (String[])collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY;
  }

  public static String replace(String inString, String oldPattern, @Nullable String newPattern) {
    if (hasLength(inString) && hasLength(oldPattern) && newPattern != null) {
      int index = inString.indexOf(oldPattern);
      if (index == -1) {
        return inString;
      } else {
        int capacity = inString.length();
        if (newPattern.length() > oldPattern.length()) {
          capacity += 16;
        }

        StringBuilder sb = new StringBuilder(capacity);
        int pos = 0;

        for(int patLen = oldPattern.length(); index >= 0; index = inString.indexOf(oldPattern, pos)) {
          sb.append(inString, pos, index);
          sb.append(newPattern);
          pos = index + patLen;
        }

        sb.append(inString, pos, inString.length());
        return sb.toString();
      }
    } else {
      return inString;
    }
  }

  @Nullable
  public static String getFilename(@Nullable String path) {
    if (path == null) {
      return null;
    } else {
      int separatorIndex = path.lastIndexOf(47);
      return separatorIndex != -1 ? path.substring(separatorIndex + 1) : path;
    }
  }

  /**
   * Normalize the path by suppressing sequences like "path/.." and
   * inner simple dots.
   * <p>The result is convenient for path comparison. For other uses,
   * notice that Windows separators ("\" and "\\") are replaced by simple slashes.
   * <p><strong>NOTE</strong> that {@code cleanPath} should not be depended
   * upon in a security context. Other mechanisms should be used to prevent
   * path-traversal issues.
   * @param path the original path
   * @return the normalized path
   */
  public static String cleanPath(String path) {
    if (!hasLength(path)) {
      return path;
    }

    String normalizedPath;
    // Optimize when there is no backslash
    if (path.indexOf('\\') != -1) {
      normalizedPath = replace(path, DOUBLE_BACKSLASHES, FOLDER_SEPARATOR);
      normalizedPath = replace(normalizedPath, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
    }
    else {
      normalizedPath = path;
    }
    String pathToUse = normalizedPath;

    // Shortcut if there is no work to do
    if (pathToUse.indexOf('.') == -1) {
      return pathToUse;
    }

    // Strip prefix from path to analyze, to not treat it as part of the
    // first path element. This is necessary to correctly parse paths like
    // "file:core/../core/io/Resource.class", where the ".." should just
    // strip the first "core" directory while keeping the "file:" prefix.
    int prefixIndex = pathToUse.indexOf(':');
    String prefix = "";
    if (prefixIndex != -1) {
      prefix = pathToUse.substring(0, prefixIndex + 1);
      if (prefix.contains(FOLDER_SEPARATOR)) {
        prefix = "";
      }
      else {
        pathToUse = pathToUse.substring(prefixIndex + 1);
      }
    }
    if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
      prefix = prefix + FOLDER_SEPARATOR;
      pathToUse = pathToUse.substring(1);
    }

    String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
    // we never require more elements than pathArray and in the common case the same number
    Deque<String> pathElements = new ArrayDeque<>(pathArray.length);
    int tops = 0;

    for (int i = pathArray.length - 1; i >= 0; i--) {
      String element = pathArray[i];
      if (CURRENT_PATH.equals(element)) {
        // Points to current directory - drop it.
      }
      else if (TOP_PATH.equals(element)) {
        // Registering top path found.
        tops++;
      }
      else {
        if (tops > 0) {
          // Merging path element with element corresponding to top path.
          tops--;
        }
        else {
          // Normal path element found.
          pathElements.addFirst(element);
        }
      }
    }

    // All path elements stayed the same - shortcut
    if (pathArray.length == pathElements.size()) {
      return normalizedPath;
    }
    // Remaining top paths need to be retained.
    for (int i = 0; i < tops; i++) {
      pathElements.addFirst(TOP_PATH);
    }
    // If nothing else left, at least explicitly point to current path.
    if (pathElements.size() == 1 && pathElements.getLast().isEmpty() && !prefix.endsWith(FOLDER_SEPARATOR)) {
      pathElements.addFirst(CURRENT_PATH);
    }

    final String joined = collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
    // avoid string concatenation with empty prefix
    return prefix.isEmpty() ? joined : prefix + joined;
  }

  public static String applyRelativePath(String path, String relativePath) {
    int separatorIndex = path.lastIndexOf(47);
    if (separatorIndex != -1) {
      String newPath = path.substring(0, separatorIndex);
      if (!relativePath.startsWith("/")) {
        newPath = newPath + "/";
      }

      return newPath + relativePath;
    } else {
      return relativePath;
    }
  }


  /**
   * Convert a {@link Collection} to a delimited {@code String} (e.g. CSV).
   * <p>Useful for {@code toString()} implementations.
   * @param coll the {@code Collection} to convert (potentially {@code null} or empty)
   * @param delim the delimiter to use (typically a ",")
   * @param prefix the {@code String} to start each element with
   * @param suffix the {@code String} to end each element with
   * @return the delimited {@code String}
   */
  public static String collectionToDelimitedString(
      @Nullable Collection<?> coll, String delim, String prefix, String suffix) {

    if (CollectionUtils.isEmpty(coll)) {
      return "";
    }

    int totalLength = coll.size() * (prefix.length() + suffix.length()) + (coll.size() - 1) * delim.length();
    for (Object element : coll) {
      totalLength += String.valueOf(element).length();
    }

    StringBuilder sb = new StringBuilder(totalLength);
    Iterator<?> it = coll.iterator();
    while (it.hasNext()) {
      sb.append(prefix).append(it.next()).append(suffix);
      if (it.hasNext()) {
        sb.append(delim);
      }
    }
    return sb.toString();
  }

  public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim) {
    return collectionToDelimitedString(coll, delim, "", "");
  }

  public static String collectionToCommaDelimitedString(@Nullable Collection<?> coll) {
    return collectionToDelimitedString(coll, ",");
  }

  public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter) {
    return delimitedListToStringArray(str, delimiter, (String)null);
  }

  /**
   * Take a {@code String} that is a delimited list and convert it into
   * a {@code String} array.
   * <p>A single {@code delimiter} may consist of more than one character,
   * but it will still be considered as a single delimiter string, rather
   * than as a bunch of potential delimiter characters, in contrast to
   * {@link #tokenizeToStringArray}.
   * @param str the input {@code String} (potentially {@code null} or empty)
   * @param delimiter the delimiter between elements (this is a single delimiter,
   * rather than a bunch individual delimiter characters)
   * @param charsToDelete a set of characters to delete; useful for deleting unwanted
   * line breaks: e.g. "\r\n\f" will delete all new lines and line feeds in a {@code String}
   * @return an array of the tokens in the list
   * @see #tokenizeToStringArray
   */
  public static String[] delimitedListToStringArray(
      @Nullable String str, @Nullable String delimiter, @Nullable String charsToDelete) {

    if (str == null) {
      return EMPTY_STRING_ARRAY;
    }
    if (delimiter == null) {
      return new String[] {str};
    }

    List<String> result = new ArrayList<>();
    if (delimiter.isEmpty()) {
      for (int i = 0; i < str.length(); i++) {
        result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
      }
    }
    else {
      int pos = 0;
      int delPos;
      while ((delPos = str.indexOf(delimiter, pos)) != -1) {
        result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
        pos = delPos + delimiter.length();
      }
      if (!str.isEmpty() && pos <= str.length()) {
        // Add rest of String, but not in case of empty input.
        result.add(deleteAny(str.substring(pos), charsToDelete));
      }
    }
    return toStringArray(result);
  }
  public static String deleteAny(String inString, @Nullable String charsToDelete) {
    if (hasLength(inString) && hasLength(charsToDelete)) {
      int lastCharIndex = 0;
      char[] result = new char[inString.length()];

      for(int i = 0; i < inString.length(); ++i) {
        char c = inString.charAt(i);
        if (charsToDelete.indexOf(c) == -1) {
          result[lastCharIndex++] = c;
        }
      }

      if (lastCharIndex == inString.length()) {
        return inString;
      } else {
        return new String(result, 0, lastCharIndex);
      }
    } else {
      return inString;
    }
  }

  /**
   * Tokenize the given {@code String} into a {@code String} array via a
   * {@link StringTokenizer}.
   * <p>Trims tokens and omits empty tokens.
   * <p>The given {@code delimiters} string can consist of any number of
   * delimiter characters. Each of those characters can be used to separate
   * tokens. A delimiter is always a single character; for multi-character
   * delimiters, consider using {@link #delimitedListToStringArray}.
   * @param str the {@code String} to tokenize (potentially {@code null} or empty)
   * @param delimiters the delimiter characters, assembled as a {@code String}
   * (each of the characters is individually considered as a delimiter)
   * @return an array of the tokens
   * @see java.util.StringTokenizer
   * @see String#trim()
   * @see #delimitedListToStringArray
   */
  public static String[] tokenizeToStringArray(@Nullable String str, String delimiters) {
    return tokenizeToStringArray(str, delimiters, true, true);
  }

  /**
   * Tokenize the given {@code String} into a {@code String} array via a
   * {@link StringTokenizer}.
   * <p>The given {@code delimiters} string can consist of any number of
   * delimiter characters. Each of those characters can be used to separate
   * tokens. A delimiter is always a single character; for multi-character
   * delimiters, consider using {@link #delimitedListToStringArray}.
   * @param str the {@code String} to tokenize (potentially {@code null} or empty)
   * @param delimiters the delimiter characters, assembled as a {@code String}
   * (each of the characters is individually considered as a delimiter)
   * @param trimTokens trim the tokens via {@link String#trim()}
   * @param ignoreEmptyTokens omit empty tokens from the result array
   * (only applies to tokens that are empty after trimming; StringTokenizer
   * will not consider subsequent delimiters as token in the first place).
   * @return an array of the tokens
   * @see java.util.StringTokenizer
   * @see String#trim()
   * @see #delimitedListToStringArray
   */
  public static String[] tokenizeToStringArray(
      @Nullable String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

    if (str == null) {
      return EMPTY_STRING_ARRAY;
    }

    StringTokenizer st = new StringTokenizer(str, delimiters);
    List<String> tokens = new ArrayList<>();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (trimTokens) {
        token = token.trim();
      }
      if (!ignoreEmptyTokens || !token.isEmpty()) {
        tokens.add(token);
      }
    }
    return toStringArray(tokens);
  }
}

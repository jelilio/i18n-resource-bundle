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
import io.github.jelilio.i18nresourcebundle.exception.NoSuchMessageException;

import java.util.Locale;

/**
 * Strategy interface for resolving messages, with support for the parameterization
 * and internationalization of such messages.
 *
 * @see io.github.jelilio.i18nresourcebundle.support.ResourceBundleMessageSource
 * @see io.github.jelilio.i18nresourcebundle.support.ReloadableResourceBundleMessageSource
 */
public interface MessageSource {
  @Nullable
  String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);

  String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;

  /**
   * Try to resolve the message using all the attributes contained within the
   * {@code MessageSourceResolvable} argument that was passed in.
   * <p>NOTE: We must throw a {@code NoSuchMessageException} on this method
   * since at the time of calling this method we aren't able to determine if the
   * {@code defaultMessage} property of the resolvable is {@code null} or not.
   * @param resolvable the value object storing attributes required to resolve a message
   * (may include a default message)
   * @param locale the locale in which to do the lookup
   * @return the resolved message (never {@code null} since even a
   * {@code MessageSourceResolvable}-provided default message needs to be non-null)
   * @throws NoSuchMessageException if no corresponding message was found
   * (and no default message was provided by the {@code MessageSourceResolvable})
   * @see MessageSourceResolvable#getCodes()
   * @see MessageSourceResolvable#getArguments()
   * @see MessageSourceResolvable#getDefaultMessage()
   * @see java.text.MessageFormat
   */
  String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;
}

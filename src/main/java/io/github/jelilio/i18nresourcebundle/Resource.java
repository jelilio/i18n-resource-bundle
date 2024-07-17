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
import io.github.jelilio.i18nresourcebundle.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * Interface for a resource descriptor that abstracts from the actual
 * type of underlying resource, such as a file or class path resource.
 *
 * <p>An InputStream can be opened for every resource if it exists in
 * physical form, but a URL or File handle can just be returned for
 * certain resources. The actual behavior is implementation-specific.
 *
 * @see #getInputStream()
 * @see #getURL()
 * @see #getURI()
 * @see #getFile()
 */

public interface Resource extends InputStreamSource {
  boolean exists();

  default boolean isReadable() {
    return this.exists();
  }

  default boolean isOpen() {
    return false;
  }

  default boolean isFile() {
    return false;
  }

  URL getURL() throws IOException;

  URI getURI() throws IOException;

  File getFile() throws IOException;

  default ReadableByteChannel readableChannel() throws IOException {
    return Channels.newChannel(this.getInputStream());
  }

  default byte[] getContentAsByteArray() throws IOException {
    return FileCopyUtils.copyToByteArray(this.getInputStream());
  }

  default String getContentAsString(Charset charset) throws IOException {
    return FileCopyUtils.copyToString(new InputStreamReader(this.getInputStream(), charset));
  }

  long contentLength() throws IOException;

  long lastModified() throws IOException;

  Resource createRelative(String relativePath) throws IOException;

  @Nullable
  String getFilename();

  String getDescription();
}

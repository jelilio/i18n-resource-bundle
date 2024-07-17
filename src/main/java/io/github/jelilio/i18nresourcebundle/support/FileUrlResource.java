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

package io.github.jelilio.i18nresourcebundle.support;

import io.github.jelilio.i18nresourcebundle.Resource;
import io.github.jelilio.i18nresourcebundle.WritableResource;
import io.github.jelilio.i18nresourcebundle.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Subclass of {@link UrlResource} which assumes file resolution, to the degree
 * of implementing the {@link WritableResource} interface for it. This resource
 * variant also caches resolved {@link File} handles from {@link #getFile()}.
 *
 * <p>This is the class resolved by {@link DefaultResourceLoader} for a "file:..."
 * URL location, allowing a downcast to {@link WritableResource} for it.
 *
 */
public class FileUrlResource extends UrlResource implements WritableResource {
  @Nullable
  private volatile File file;

  public FileUrlResource(URL url) {
    super(url);
  }

  public FileUrlResource(String location) throws MalformedURLException {
    super("file", location);
  }

  public File getFile() throws IOException {
    File file = this.file;
    if (file != null) {
      return file;
    } else {
      file = super.getFile();
      this.file = file;
      return file;
    }
  }

  public boolean isWritable() {
    try {
      File file = this.getFile();
      return file.canWrite() && !file.isDirectory();
    } catch (IOException var2) {
      return false;
    }
  }

  public OutputStream getOutputStream() throws IOException {
    return Files.newOutputStream(this.getFile().toPath());
  }

  public WritableByteChannel writableChannel() throws IOException {
    return FileChannel.open(this.getFile().toPath(), StandardOpenOption.WRITE);
  }

  public Resource createRelative(String relativePath) throws MalformedURLException {
    return new FileUrlResource(this.createRelativeURL(relativePath));
  }
}

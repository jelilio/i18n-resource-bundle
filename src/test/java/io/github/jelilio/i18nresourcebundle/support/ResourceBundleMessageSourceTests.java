package io.github.jelilio.i18nresourcebundle.support;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import io.github.jelilio.i18nresourcebundle.exception.NoSuchMessageException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ResourceBundleMessageSourceTests {

  @Test
  public void testResourceBundleMessageSourceStandalone() {
    ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
    ms.setBasename("messages");
    assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
    assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
  }

  @Test
  public void testResourceBundleMessageSourceWithWhitespaceInBasename() {
    ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
    ms.setBasename("  messages  ");
    assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
    assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
  }

  @Test
  public void testResourceBundleMessageSourceWithDefaultCharset() {
    ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
    ms.setBasename("messages");
    ms.setDefaultEncoding("ISO-8859-1");
    assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
    assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
  }

  @Test
  public void testResourceBundleMessageSourceWithInappropriateDefaultCharset() {
    ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
    ms.setBasename("messages");
    ms.setDefaultEncoding("argh");
    ms.setFallbackToSystemLocale(false);
    try {
      ms.getMessage("code1", null, Locale.ENGLISH);
      fail("Should have thrown NoSuchMessageException");
    } catch (NoSuchMessageException ex) {
      // expected
    }
  }

  @AfterEach
  public void tearDown() {
    ResourceBundle.clearCache();
  }
}

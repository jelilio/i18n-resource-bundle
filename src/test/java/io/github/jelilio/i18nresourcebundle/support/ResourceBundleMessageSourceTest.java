package io.github.jelilio.i18nresourcebundle.support;

import io.github.jelilio.i18nresourcebundle.MessageSourceResolvable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceBundleMessageSourceTest {
  private static final String USER_NAME = "user.name";
  private static final String WELCOME = "welcome";
  private static final String NOT_EXIST = "notExist";
  private static final String HOTEL_NAME = "hotel.name";

  private ResourceBundleMessageSource messageSource;

  @BeforeEach
  public void setUp(){
    messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames("message", "another");
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setFallbackToSystemLocale(false);
  }

  @Test
  public void getMessageWithOutArgs() throws Exception {
    String english = messageSource.getMessage(USER_NAME, null, Locale.US);
    String chinese = messageSource.getMessage(USER_NAME, null, Locale.SIMPLIFIED_CHINESE);

    assertEquals("username-us", english);
    assertEquals("用户名", chinese);
  }

  @Test
  public void getMessageWithOutArgsFallbackToDefaultMessage() throws Exception {
    String english = messageSource.getMessage(USER_NAME, null, Locale.UK);
    String chinese = messageSource.getMessage(USER_NAME, null, Locale.SIMPLIFIED_CHINESE);

    assertEquals("username", english);
    assertEquals("用户名", chinese);
  }

  @Test
  public void getMessageWithArgs() throws Exception {
    Object[] args = new Object[3];
    args[0] = "Ryan";
    args[1] = "EasyI18n";
    args[2] = "\uD83D\uDE1C";
    String english = messageSource.getMessage(WELCOME, args, Locale.US);
    String chinese = messageSource.getMessage(WELCOME, args, Locale.SIMPLIFIED_CHINESE);

    assertEquals("Welcome Ryan to EasyI18n, \uD83D\uDE1C", english);
    assertEquals("欢迎 Ryan 来到 EasyI18n, \uD83D\uDE1C", chinese);
  }

  @Test
  public void getMessageExistWithDefaultMessageShouldGetExistVal() throws Exception {
    String english = messageSource.getMessage(HOTEL_NAME, null,"default value", Locale.US);
    String chinese = messageSource.getMessage(HOTEL_NAME, null,"默认值", Locale.SIMPLIFIED_CHINESE);

    assertEquals("hotel name[us]", english);
    assertEquals("酒店名称", chinese);
  }

  @Test
  public void getMessageNotExistWithDefaultMessageShouldGetDefaultVal() throws Exception {
    String english = messageSource.getMessage(NOT_EXIST, null,"default value", Locale.US);
    String chinese = messageSource.getMessage(NOT_EXIST, null,"默认值", Locale.SIMPLIFIED_CHINESE);

    assertEquals("default value", english);
    assertEquals("默认值", chinese);
  }

  @Test
  public void getMessageWithResolvable(){
    String english = messageSource.getMessage(getResolvable(), Locale.US);
    String chinese = messageSource.getMessage(getResolvable(), Locale.SIMPLIFIED_CHINESE);

    assertEquals("username-us", english);
    assertEquals("用户名", chinese);
  }

  private MessageSourceResolvable getResolvable() {
    return new MessageSourceResolvable() {
      @Override
      public String[] getCodes() {
        return new String[]{"notExist","notExist","user.name"};
      }

      @Override
      public Object[] getArguments() {
        return new Object[0];
      }

      @Override
      public String getDefaultMessage() {
        return null;
      }
    };
  }
}

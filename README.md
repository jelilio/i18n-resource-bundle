# i18n-resource-bundle

This library provides utility's classes to implement internationalisation (i18n) in Java frameworks like Quarkus.

## Usage
### Maven:

````xml
<dependency>
    <groupId>io.github.jelilio</groupId>
    <artifactId>i18n-resource-bundle</artifactId>
    <version>0.0.2</version>
</dependency>
````

### Gradle:

````    
implementation 'io.github.jelilio:i18n-resource-bundle:0.0.2'
````  

### Define "messages.properties" file in classpath

````    
welcome.text=Welcome {0} to a new world
````  

### Create MessageSource definition

````java
public MessageSource messageSource() {
  MessageSource messageSource  = new ResourceBundleMessageSource();
  messageSource.setBasename("classpath:messages");
  messageSource.setDefaultEncoding("UTF-8");
  messageSource.setFallbackToSystemLocale(false);
  return messageSource;
}
````

### Localizing a message

````java
public void greeting(String name) {
  MessageSource messageSource  = messageSource();
  String message = messageSource.getMessage("welcome.text", new String[]{name}, Locale.US);
}
````

### Contributor
* @jelilio

### License

This library is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
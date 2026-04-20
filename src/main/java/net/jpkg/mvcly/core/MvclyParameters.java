package net.jpkg.mvcly.core;

/**
 * This class provides access to the Application Parameters and defines the default values that can
 * be overwritten using the JVM start up parameter, Example: java -jar app.jar -Dport=8882
 * -Denable_cache=true
 */
public enum MvclyParameters {

  PORT("port", "8080"),
  ENABLE_CACHE("enable_cache", "false"),
  TEMPLATES_PATH("templates_path", "templates"),
  FILES_PATH("files_path", "files");

  private final String name;
  private final String defaultValue;

  private MvclyParameters(String name, String defaultValue) {
    this.name = name;
    this.defaultValue = defaultValue;
  }

  public String getName() {
    return name;
  }

  public String get() {
    return System.getProperty(name, defaultValue);
  }
}

package onl.andres.mvcly.core;

public enum MvclyParameters {

	PORT("port", "8080"),
    ENABLE_CACHE("enable_cache", "false"),
    TEMPLATES_PATH("templates_path", "file://templates"),
    FILES_PATH("files_path", "file://files");

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

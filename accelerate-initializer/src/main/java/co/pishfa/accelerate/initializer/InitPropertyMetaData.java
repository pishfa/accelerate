package co.pishfa.accelerate.initializer;

import org.apache.commons.lang3.Validate;

/**
 * Contains information about a property of entity during initialization. This class is used in {@link Initializer}.
 * 
 * @author Taha Ghasemi
 * 
 */
public class InitPropertyMetaData {
	private String name;
	private String alias;
	private String defaultValue;
	private final boolean dynamic;

	public InitPropertyMetaData(String name, String alias, String defaultValue, boolean dynamic) {
		Validate.notNull(name, "Name of property is required");
		Validate.notNull(alias, "Alias of property is required");

		this.name = name;
		this.alias = alias;
		this.defaultValue = defaultValue;
		this.dynamic = dynamic;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
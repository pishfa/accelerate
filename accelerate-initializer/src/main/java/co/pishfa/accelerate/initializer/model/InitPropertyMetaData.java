package co.pishfa.accelerate.initializer.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import co.pishfa.accelerate.initializer.core.DefaultInitializer;

/**
 * Contains information about a property of entity during initialization. This class is used in
 * {@link DefaultInitializer}.
 * 
 * @author Taha Ghasemi
 * 
 */
public class InitPropertyMetadata {
	private String name;
	private String alias;
	private String defaultValue;
	private final boolean dynamic;

	public InitPropertyMetadata(String name, String alias, String defaultValue, boolean dynamic) {
		Validate.notNull(name, "Name of property is required");
		this.name = name;
		this.alias = StringUtils.isEmpty(alias) ? name : alias;
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
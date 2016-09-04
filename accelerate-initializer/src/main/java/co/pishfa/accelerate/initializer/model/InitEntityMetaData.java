package co.pishfa.accelerate.initializer.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains metadata information about an init entity during initialization.
 * 
 * @author Taha Ghasemi
 * 
 */
public class InitEntityMetaData {

	private static final Logger log = LoggerFactory.getLogger(InitEntityMetaData.class);

	private String alias;
	private Class<?> entityClass;
	private String key;
	private Map<String, InitPropertyMetaData> propertiesByAlias = new HashMap<>();

	private final List<InitPropertyMetaData> properties = new ArrayList<>();

	public InitEntityMetaData(String alias, Class<?> entityClass, String key) {
		Validate.notNull(entityClass);

		this.entityClass = entityClass;
		this.alias = StringUtils.isEmpty(alias) ? entityClass.getSimpleName() : alias;
		this.key = key;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public InitPropertyMetaData getProperty(String propertyAlias) {
		return propertiesByAlias.get(propertyAlias);
	}

	public void setPropertiesByAlias(Map<String, InitPropertyMetaData> properties) {
		this.propertiesByAlias = properties;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String unique) {
		this.key = unique;
	}

	public List<InitPropertyMetaData> getProperties() {
		return properties;
	}

	/**
	 * Adds a new property. Override the previous one with the same alias, if any.
	 * 
	 */
	public void addProperty(InitPropertyMetaData property) {
		InitPropertyMetaData prevProperty = propertiesByAlias.get(property.getAlias());
		if (prevProperty != null) {
			log.warn("Overriding the property with alias {} in {}", property.getAlias(), entityClass);
			properties.remove(prevProperty);
		}
		properties.add(property);
		propertiesByAlias.put(property.getAlias(), property);
	}
}
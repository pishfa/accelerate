package co.pishfa.accelerate.initializer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private String unique;
	private Map<String, InitPropertyMetaData> propertiesByAlias = new HashMap<String, InitPropertyMetaData>();

	private final List<InitPropertyMetaData> properties = new ArrayList<InitPropertyMetaData>();

	public InitEntityMetaData(String alias, Class<?> entityClass, String unique) {
		Validate.notNull(entityClass);

		this.entityClass = entityClass;
		this.alias = StringUtils.isEmpty(alias) ? entityClass.getSimpleName() : alias;
		this.unique = unique;
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

	public Map<String, InitPropertyMetaData> getPropertiesByAlias() {
		return propertiesByAlias;
	}

	public void setPropertiesByAlias(Map<String, InitPropertyMetaData> properteis) {
		this.propertiesByAlias = properteis;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public List<InitPropertyMetaData> getProperties() {
		return properties;
	}

	/**
	 * Adds a new property. Override the previous one with the same alias, if any.
	 * 
	 */
	public void addProperty(InitPropertyMetaData property) {
		InitPropertyMetaData prevPropery = propertiesByAlias.get(property.getAlias());
		if (prevPropery != null) {
			log.warn("Overriding the property with alias {} in {}", property.getAlias(), entityClass);
			properties.remove(prevPropery);
		}
		properties.add(property);
		propertiesByAlias.put(property.getAlias(), property);
	}
}
package co.pishfa.accelerate.initializer.core;

import co.pishfa.accelerate.initializer.api.InitializerFactory;
import co.pishfa.accelerate.initializer.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Reads init metadata from annotated classes
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class AnnotationMetadataReader {

	private static final Logger log = LoggerFactory.getLogger(AnnotationMetadataReader.class);

	protected InitializerFactory factory;

	public AnnotationMetadataReader(InitializerFactory factory) {
		this.factory = factory;
	}

	/**
	 * Processes the provided entity classes.
	 * 
	 * @param entityClasses
	 */
	public void processEntityClasses(List<Class<?>> entityClasses) {
		for (Class<?> entityClass : entityClasses) {
			try {
				processEntityClass(entityClass);
			} catch (Exception e) {
				log.error("Could not process entity class " + entityClass, e);
			}
		}
	}

	/**
	 * This is called whenever a new entity class is encountered.
	 * 
	 */
	protected void processEntityClass(Class<?> entityClass) {
		InitEntity initEntity = entityClass.getAnnotation(InitEntity.class);
		String key = null;
		String alias = null;
		if (initEntity != null) {
			key = initEntity.key();
			alias = initEntity.alias();
		}
		if(StringUtils.isEmpty(key))
			key = findEntityKeys(entityClass);

		InitEntityMetadata initEntityMetadata = new InitEntityMetadata(alias, entityClass, key);
		factory.addInitEntity(initEntityMetadata);
		addInitProperties(entityClass, initEntityMetadata);
	}

	protected String findEntityKeys(Class<?> entityClass) {
		StringBuilder keyParts = new StringBuilder();
		// find keys
		boolean first = true;
		for (Field field : entityClass.getDeclaredFields()) {
			if (!first) {
				keyParts.append(',');
			}
			if (field.getAnnotation(InitKey.class) != null) {
				keyParts.append(field.getName());
				first = false;
			}
		}
		return first ? null : keyParts.toString();
	}

	/**
	 * Adds the init properties of the given entityClass plus all its parents that annotated with {@link InitEntity} .
	 */
	protected void addInitProperties(Class<?> entityClass, InitEntityMetadata initEntityMetadata) {
		Class<?> parent = entityClass.getSuperclass();
		if (parent.isAnnotationPresent(InitEntity.class)) {
			addInitProperties(parent, initEntityMetadata);
		}

		InitEntity initEntity = entityClass.getAnnotation(InitEntity.class);
		if (initEntity != null) {
			for (InitProperty initProperty : initEntity.properties()) {
				initEntityMetadata.addProperty(processInitProperty(null, initProperty, entityClass));
			}
		}

		for (Field field : entityClass.getDeclaredFields()) {
			InitProperty initProperty = field.getAnnotation(InitProperty.class);
			if (initProperty != null) {
				initEntityMetadata.addProperty(processInitProperty(field, initProperty, entityClass));
			}
		}
	}

	/**
	 * 
	 * @param field
	 *            may be null, if the annotation is present in the InitEntity
	 * @param initProperty
	 * @param entityClass
	 *            only used for debugging purposes.
	 * @return
	 */
	private InitPropertyMetadata processInitProperty(Field field, InitProperty initProperty, Class<?> entityClass) {
		String fieldName = null;
		if (field == null) {
			fieldName = initProperty.name();
			if (StringUtils.isEmpty(fieldName)) {
				throw new IllegalArgumentException(
						"Property name of InitProperty is mandantory when used inside the InitEntity " + entityClass);
			}
		} else {
			fieldName = StringUtils.isEmpty(initProperty.name()) ? field.getName() : initProperty.name();
		}
		InitPropertyMetadata initPropertyMetadata = new InitPropertyMetadata(fieldName, initProperty.alias(),
				initProperty.value(), initProperty.dynamic());
		return initPropertyMetadata;
	}

}

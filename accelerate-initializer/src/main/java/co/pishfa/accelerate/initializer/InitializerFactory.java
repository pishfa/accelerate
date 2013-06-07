/**
 * 
 */
package co.pishfa.accelerate.initializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ExpressionFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.odysseus.el.ExpressionFactoryImpl;

/**
 * A thread-safe factory for {@link Initializer}. It also holds the configurations. This factory has a fluent interface
 * e.g you may use it in the following way to obtain an initializer instance:
 * 
 * <p>
 * Initializer initializer = new
 * InitializerFactory().entityClasses(Book.class,Category.class).configFile(config).uniquePropertyName
 * ("name").create(initListener);
 * </p>
 * 
 * @author Taha Ghasemi
 * 
 */
public class InitializerFactory {

	private static final Logger log = LoggerFactory.getLogger(InitializerFactory.class);
	private static final Namespace PISHFA_NS = Namespace.getNamespace("http://pishfa.co");

	private final Map<String, InitEntityMetaData> aliasToInitEntity = new HashMap<>();
	// TODO expression factory should be loaded using service discovery and it should be optional
	private final ExpressionFactory engine = new ExpressionFactoryImpl();
	private boolean incremental = false;
	private boolean autoAnchor = true;
	private String uniquePropertyName = "*";

	/**
	 * Optional list of classes for annotation processing.
	 */
	public InitializerFactory entityClasses(Class<?>... entityClasses) {
		Validate.notNull(entityClasses);
		processEntityClasses(Arrays.asList(entityClasses));
		return this;
	}

	/**
	 * Optional list of classes for annotation processing.
	 */
	public InitializerFactory entityClasses(List<Class<?>> entityClasses) {
		Validate.notNull(entityClasses);
		processEntityClasses(entityClasses);
		return this;
	}

	/**
	 * The name of resource that represents xml-file based configuration which conforms to initializer-config.xsd. The
	 * resource is loaded using Thread.currentThread().getContextClassLoader().getResourceAsStream.
	 */
	public InitializerFactory configFile(String resourceName) throws Exception {
		Validate.notNull(resourceName);
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
			return configFile(is);
		}
	}

	/**
	 * Optional xml-file based configuration which conforms to initializer-config.xsd.
	 */
	public InitializerFactory configFile(File configFile) throws Exception {
		Validate.notNull(configFile);
		try (InputStream is = new FileInputStream(configFile)) {
			return configFile(is);
		}
	}

	/**
	 * Optional xml-file based configuration which conforms to initializer-config.xsd. Note that you are responsible to
	 * close this resource.
	 */
	public InitializerFactory configFile(InputStream configFile) throws Exception {
		Validate.notNull(configFile);
		processConfigFile(configFile);
		return this;
	}

	/**
	 * Comma separated list of property names that makes an instance of this entity unique. Could also be *. This is
	 * used in both auto-anchoring and loading of objects. Default value is *.
	 */
	public InitializerFactory uniquePropertyName(String uniquePropertyName) {
		this.uniquePropertyName = uniquePropertyName;
		return this;
	}

	/**
	 * Initializer automatically creates an anchor like this for each entity EntityAlias:unique1_unique2_uniqe3,
	 * provided that all unique values (which their property names are either specified here or per entity) are not
	 * null. Default value is true.
	 */
	public InitializerFactory autoAnchor(boolean autoAnchor) {
		this.autoAnchor = autoAnchor;
		return this;
	}

	/**
	 * If true, {@link Initializer} first tries to find objects by supplying unique values (which their property names
	 * are either specified here or per entity) to the listener and if it fails, then creates them. Default value is
	 * false.
	 */
	public InitializerFactory incremental(boolean incremental) {
		this.incremental = incremental;
		return this;
	}

	private void processEntityClasses(List<Class<?>> entityClasses) {
		for (Class<?> entityClass : entityClasses) {
			try {
				processEntityClass(entityClass);
			} catch (Exception e) {
				log.error("Could not process entity class " + entityClass, e);
			}
		}
	}

	/**
	 * This is called whenever a new annotated type is scanned.
	 * 
	 */
	private void processEntityClass(Class<?> entityClass) {
		InitEntity initEntity = entityClass.getAnnotation(InitEntity.class);
		String unique = null;
		String alias = null;
		if (initEntity != null) {
			unique = StringUtils.defaultIfEmpty(initEntity.unique(), null);
			alias = initEntity.alias();
		}
		alias = StringUtils.defaultIfEmpty(alias, entityClass.getSimpleName());
		InitEntityMetaData initEntityMetaData = new InitEntityMetaData(alias, entityClass, unique);
		addInitEntity(initEntityMetaData);
		addInitProperties(entityClass, initEntityMetaData);
	}

	/**
	 * Adds the init properties of given entityClass plus all its parents that annotated with {@link InitEntity} .
	 */
	protected void addInitProperties(Class<?> entityClass, InitEntityMetaData initEntityMetaData) {
		Class<?> parent = entityClass.getSuperclass();
		if (parent.isAnnotationPresent(InitEntity.class)) {
			addInitProperties(parent, initEntityMetaData);
		}

		InitEntity initEntity = entityClass.getAnnotation(InitEntity.class);
		if (initEntity != null) {
			for (InitProperty initProperty : initEntity.value()) {
				initEntityMetaData.addProperty(processInitProperty(null, initProperty, entityClass));
			}
		}

		for (Field field : entityClass.getDeclaredFields()) {
			InitProperty initProperty = field.getAnnotation(InitProperty.class);
			if (initProperty != null) {
				initEntityMetaData.addProperty(processInitProperty(field, initProperty, entityClass));
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
	private static InitPropertyMetaData processInitProperty(Field field, InitProperty initProperty, Class<?> entityClass) {
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
		String alias = StringUtils.isEmpty(initProperty.alias()) ? fieldName : initProperty.alias();
		InitPropertyMetaData initPropertyMetaData = new InitPropertyMetaData(fieldName, alias, initProperty.value(),
				initProperty.dynamic());
		return initPropertyMetaData;
	}

	private void processConfigFile(InputStream configFile) throws JDOMException, IOException, ClassNotFoundException,
			URISyntaxException {
		File xsd = new File(getClass().getResource("/initializer-config.xsd").toURI());
		XMLReaderXSDFactory xsdFactory = new XMLReaderXSDFactory(xsd);
		SAXBuilder builder = new SAXBuilder(xsdFactory);
		Element config = builder.build(configFile).getRootElement();
		for (Element entityElem : config.getChildren("entity", PISHFA_NS)) {
			String entityClazz = entityElem.getAttributeValue("class");
			String entityAlias = entityElem.getAttributeValue("alias");
			if (StringUtils.isEmpty(entityAlias)) {
				int index = entityClazz.lastIndexOf('.');
				if (index >= 0) {
					entityAlias = entityClazz.substring(index + 1);
				} else {
					entityAlias = entityClazz;
				}
			}
			String entityUnique = entityElem.getAttributeValue("unique");
			InitEntityMetaData initEntity = new InitEntityMetaData(entityAlias, Class.forName(entityClazz),
					entityUnique);
			String inherits = entityElem.getAttributeValue("inherits");
			if (!StringUtils.isEmpty(inherits)) {
				for (String inherit : inherits.split(",")) {
					InitEntityMetaData inheritEntity = aliasToInitEntity.get(inherit);
					if (inheritEntity != null) {
						for (InitPropertyMetaData property : inheritEntity.getProperties()) {
							initEntity.addProperty(property);
						}
					} else {
						log.error("Invalid alias " + inherit + " defined in inherits of entity "
								+ initEntity.getAlias());
					}
				}
			}
			addInitEntity(initEntity);
			// read properties of an entity
			for (Element propertyElem : entityElem.getChildren("property", PISHFA_NS)) {
				String propName = propertyElem.getAttributeValue("name");
				String propAlias = propertyElem.getAttributeValue("alias");
				String propDefault = propertyElem.getAttributeValue("default");
				String propDynamic = propertyElem.getAttributeValue("dynamic");
				InitPropertyMetaData initProperty = new InitPropertyMetaData(propName, StringUtils.defaultString(
						propAlias, propName), propDefault, !"false".equals(propDynamic));
				initEntity.addProperty(initProperty);
			}
		}
	}

	public void addInitEntity(InitEntityMetaData initEntity) {
		String alias = initEntity.getAlias();
		if (aliasToInitEntity.containsKey(alias)) {
			log.warn("Duplicate entity alias {} this will override the previose one.", alias);
		}
		aliasToInitEntity.put(alias, initEntity);
	}

	/**
	 * @return the engine
	 */
	public ExpressionFactory getEngine() {
		return engine;
	}

	/**
	 * @return the incremental
	 */
	public boolean isIncremental() {
		return incremental;
	}

	/**
	 * @return the autoAnchor
	 */
	public boolean isAutoAnchor() {
		return autoAnchor;
	}

	public String getUniquePropertyName() {
		return uniquePropertyName;
	}

	public InitEntityMetaData getInitEntityByAlias(String alias) {
		return aliasToInitEntity.get(alias);
	}

	public Initializer create(InitListener listener, Map<String, Object> contextVars) {
		return new Initializer(this, listener, contextVars);
	}

	public Initializer create(InitListener listener) {
		return new Initializer(this, listener, null);
	}

	public InitEntityMetaData getInitEntityByClass(Class<?> entityClass) {
		for (InitEntityMetaData initEntity : aliasToInitEntity.values()) {
			if (entityClass.equals(initEntity.getEntityClass())) {
				return initEntity;
			}
		}
		return null;

	}

}

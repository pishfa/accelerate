/**
 * 
 */
package co.pishfa.accelerate.initializer.api;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ExpressionFactory;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.pishfa.accelerate.initializer.core.AnnotationMetaDataReader;
import co.pishfa.accelerate.initializer.core.DefaultInitializer;
import co.pishfa.accelerate.initializer.core.XmlMetaDataReader;
import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitEntityMetaData;
import co.pishfa.accelerate.initializer.util.Input;

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

	private final Map<String, InitEntityMetaData> aliasToInitEntity = new HashMap<>();
	private final Map<Class<?>, InitEntityMetaData> classToInitEntity = new HashMap<>();

	private ExpressionFactory expressionFactory;
	private boolean incremental = false;
	private boolean autoAnchor = true;
	private String key = null;

	public InitializerFactory() {
		expressionFactory = ExpressionFactory.newInstance();
	}

	/**
	 * List of entity classes for annotation processing. It is not required that the provided classes are annotated with
	 * InitEnity. If an init entity with the same alias is already exits, it will be overridden. If the parent class of
	 * an entity class is annotated with {@link InitEntity}, its parent is also processed.
	 */
	public InitializerFactory entityClasses(Class<?>... entityClasses) {
		Validate.notNull(entityClasses);
		processEntityClasses(Arrays.asList(entityClasses));
		return this;
	}

	/**
	 * List of entity classes for annotation processing. It is not required that the provided classes are annotated with
	 * InitEnity. If an init entity with the same alias is already exits, it will be overridden. If the parent class of
	 * an entity class is annotated with {@link InitEntity}, its parent is also processed.
	 */
	public InitializerFactory entityClasses(List<Class<?>> entityClasses) {
		Validate.notNull(entityClasses);
		processEntityClasses(entityClasses);
		return this;
	}

	protected void processEntityClasses(List<Class<?>> entityClasses) {
		new AnnotationMetaDataReader(this).processEntityClasses(entityClasses);
	}

	/**
	 * Xml-file based configuration which conforms to initializer-config.xsd. If an init entity with the same alias is
	 * already exits, it will be overridden.
	 */
	public InitializerFactory metadata(String reasourceName) throws Exception {
		processMetadataFile(Input.resource(reasourceName), true);
		return this;
	}

	/**
	 * Xml-file based configuration which conforms to initializer-config.xsd. The encoding is determined from xml
	 * declaration. Note that you are responsible to close this resource if autoClose if false. If an init entity with
	 * the same alias is already exits, it will be overridden.
	 */
	public InitializerFactory metadata(InputStream metadataFile, boolean autoClose) throws Exception {
		Validate.notNull(metadataFile);

		processMetadataFile(metadataFile, autoClose);
		return this;

	}

	protected void processMetadataFile(InputStream input, boolean autoClose) throws Exception {
		try {
			new XmlMetaDataReader(this).processMetadata(input);
		} finally {
			if (autoClose) {
				input.close();
			}
		}
	}

	/**
	 * Comma separated list of property names that make an instance of this entity unique. Can be * which means all
	 * properties with not null value. This is used in both auto-anchoring and loading of objects. If the specified
	 * value is null or empty, it means that entities by default should not participate in auto-anchoring or loading
	 * modes unless they provide their own anchoring. Default value is null. Each entity type may override this property
	 * by using key attribute.
	 */
	public InitializerFactory key(String key) {
		this.key = key;
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

	/**
	 * Introduces a new initEntity. If an initEntity with the same alias already exists, this will override it.
	 * 
	 * @param initEntity
	 */
	public void addInitEntity(InitEntityMetaData initEntity) {
		Validate.notNull(initEntity);

		String alias = initEntity.getAlias();
		if (aliasToInitEntity.containsKey(alias)) {
			log.warn("Duplicate entity alias {} this will override the previose one.", alias);
		}
		aliasToInitEntity.put(alias, initEntity);
		classToInitEntity.put(initEntity.getEntityClass(), initEntity);
	}

	/**
	 * Sets the javax.el.ExpressionFactory for evaluating dynamic expressions.
	 */
	public InitializerFactory expressionFactory(ExpressionFactory expressionFactory) {
		this.expressionFactory = expressionFactory;
		return this;
	}

	/**
	 * @return the expression engine
	 */
	public ExpressionFactory getExpressionFactory() {
		return expressionFactory;
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

	public String getKey() {
		return key;
	}

	public Initializer create(InitListener listener, Map<String, Object> contextVars) {
		return new DefaultInitializer(this, listener, contextVars);
	}

	public Initializer create(InitListener listener) {
		return new DefaultInitializer(this, listener, null);
	}

	public Initializer create() {
		return new DefaultInitializer(this, null, null);
	}

	/**
	 * @return the InitEntityMetaData with the given alias.
	 */
	public InitEntityMetaData getInitEntityByAlias(String alias) {
		// null key is allowed
		return aliasToInitEntity.get(alias);
	}

	/**
	 * @return the InitEntityMetaData corresponding to the entityClass provided
	 */
	public InitEntityMetaData getInitEntityByClass(Class<?> entityClass) {
		// null key is allowed
		return classToInitEntity.get(entityClass);

	}

}

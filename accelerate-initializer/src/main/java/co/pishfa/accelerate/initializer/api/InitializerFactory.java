/**
 * 
 */
package co.pishfa.accelerate.initializer.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

import javax.el.ExpressionFactory;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.pishfa.accelerate.initializer.core.AnnotationMetaDataReader;
import co.pishfa.accelerate.initializer.core.DefaultInitializer;
import co.pishfa.accelerate.initializer.core.XmlMetaDataReader;
import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitEntityMetaData;

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
	private ExpressionFactory expressionFactory;
	private boolean incremental = false;
	private boolean autoAnchor = true;
	private String uniquePropertyName = null;

	public InitializerFactory() {
		try {
			expressionFactory = ServiceLoader.load(ExpressionFactory.class).iterator().next();
		} catch (NoSuchElementException e) {
		}
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
	 * The name of resource that represents xml-file based configuration which conforms to initializer-config.xsd. The
	 * resource is loaded using Thread.currentThread().getContextClassLoader().getResourceAsStream. If an init entity
	 * with the same alias is already exits, it will be overridden.
	 */
	public InitializerFactory metadata(String resourceName) throws Exception {
		Validate.notNull(resourceName);
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
			return metadata(is);
		}
	}

	/**
	 * Optional xml-file based configuration which conforms to initializer-config.xsd. If an init entity with the same
	 * alias is already exits, it will be overridden.
	 */
	public InitializerFactory metadata(File metadataFile) throws Exception {
		Validate.notNull(metadataFile);
		try (InputStream is = new FileInputStream(metadataFile)) {
			return metadata(is);
		}
	}

	/**
	 * Optional xml-file based configuration which conforms to initializer-config.xsd. Note that you are responsible to
	 * close this resource. If an init entity with the same alias is already exits, it will be overridden.
	 */
	public InitializerFactory metadata(InputStream metadataFile) throws Exception {
		Validate.notNull(metadataFile);
		processMetadataFile(metadataFile);
		return this;
	}

	protected void processMetadataFile(InputStream metadataFile) throws Exception {
		new XmlMetaDataReader(this).processMetadata(metadataFile);
	}

	/**
	 * Comma separated list of property names that make an instance of this entity unique. Can be * which means all
	 * properties with not null value. This is used in both auto-anchoring and loading of objects. If the specified
	 * value is null, it means that the target entity should not participate in auto-anchoring or loading modes. Default
	 * value is null.
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

	public String getUniquePropertyName() {
		return uniquePropertyName;
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
		return aliasToInitEntity.get(alias);
	}

	/**
	 * @return the InitEntityMetaData corresponding to the entityClass provided
	 */
	public InitEntityMetaData getInitEntityByClass(Class<?> entityClass) {
		for (InitEntityMetaData initEntity : aliasToInitEntity.values()) {
			if (entityClass.equals(initEntity.getEntityClass())) {
				return initEntity;
			}
		}
		return null;

	}

}

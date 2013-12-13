/**
 * 
 */
package co.pishfa.accelerate.initializer.core;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.el.ExpressionFactory;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.pishfa.accelerate.initializer.api.InitListener;
import co.pishfa.accelerate.initializer.api.Initializer;
import co.pishfa.accelerate.initializer.api.InitializerFactory;
import co.pishfa.accelerate.initializer.model.InitAnnotation;
import co.pishfa.accelerate.initializer.model.InitEntityMetaData;
import co.pishfa.accelerate.initializer.model.InitPropertyMetaData;
import co.pishfa.accelerate.initializer.util.Input;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class DefaultInitializer implements Initializer {

	private abstract class ProcessEntity {

		public ProcessEntity parent;
		public InitEntityMetaData metadata;
		public Object value;
		public Map<String, ProcessProperty> properties = new HashMap<>();

		public abstract String getEntityName();

		public abstract Map<String, Object> getAttributes();

		public abstract Object getAttributeValue(String string);

		public abstract List<? extends ProcessEntity> getChildren();

		/**
		 * finds the unifying attributes. By default, it is factory.getUniquePropertyName().
		 * 
		 */
		protected String[] getKeyProperties() {
			String key = StringUtils.defaultIfEmpty(metadata.getKey(), factory.getKeyPropertyName());
			if (StringUtils.isEmpty(key)) {
				return null;
			}

			if ("*".equals(key)) {
				StringBuilder ustr = new StringBuilder();
				for (String name : properties.keySet()) {
					if (!isReservedAttribute(name)) {
						ustr.append(",").append(name);
					}
				}
				key = ustr.deleteCharAt(0).toString();
			}

			String[] properties = key.split(",");
			return properties;
		}

		public abstract String getTypeName();
	}

	private class XmlProcessEntity extends ProcessEntity {
		private final Element element;

		public XmlProcessEntity(Element element) {
			this.element = element;
			this.metadata = factory.getInitEntityByAlias(getEntityName());
		}

		@Override
		public String getEntityName() {
			return element.getName();
		}

		@Override
		public Map<String, Object> getAttributes() {
			Map<String, Object> res = new HashMap<>();
			for (Attribute attr : element.getAttributes()) {
				res.put(attr.getName(), attr.getValue());
			}
			return res;
		}

		@Override
		public String getAttributeValue(String name) {
			return element.getAttributeValue(name);
		}

		@Override
		public List<XmlProcessEntity> getChildren() {
			List<XmlProcessEntity> result = new ArrayList<>();
			for (Element child : element.getChildren()) {
				XmlProcessEntity childEntity = new XmlProcessEntity(child);
				childEntity.parent = this;
				result.add(childEntity);
			}
			return result;
		}

		@Override
		public String getTypeName() {
			return null;
		}
	}

	private class AnnotationProcessEntity extends ProcessEntity {
		private final Class<?> data;
		private Annotation dataAnnotation;

		public AnnotationProcessEntity(Class<?> data) {
			this.data = data;
			// finds the init annotation above data
			for (Annotation annotation : data.getAnnotations()) {
				if (annotation.annotationType().isAnnotationPresent(InitAnnotation.class)) {
					this.dataAnnotation = annotation;
					break;
				}
			}
			setDataMetadata();
		}

		@Override
		public Map<String, Object> getAttributes() {
			Map<String, Object> result = new HashMap<>();
			for (Method method : dataAnnotation.annotationType().getDeclaredMethods()) {
				try {
					result.put(method.getName(), method.invoke(dataAnnotation));
				} catch (Exception e) {
					log.error("", e);
				}
			}
			return result;
		}

		@Override
		public String getEntityName() {
			Class<?> entityClass = dataAnnotation.annotationType().getAnnotation(InitAnnotation.class).value();
			return entityClass == null ? dataAnnotation.annotationType().getSimpleName() : entityClass.getSimpleName();
		}

		@Override
		public Object getAttributeValue(String name) {
			try {
				return dataAnnotation.annotationType().getMethod(name).invoke(dataAnnotation);
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		public List<AnnotationProcessEntity> getChildren() {
			List<AnnotationProcessEntity> result = new ArrayList<>();
			for (Class<?> child : data.getDeclaredClasses()) {
				AnnotationProcessEntity childEntity = new AnnotationProcessEntity(child);
				childEntity.parent = this;
				result.add(childEntity);
			}
			return result;
		}

		private void setDataMetadata() {
			if (dataAnnotation == null) {
				this.metadata = null;
			} else {
				InitAnnotation initAnnotation = dataAnnotation.annotationType().getAnnotation(InitAnnotation.class);
				if (initAnnotation.value() != Object.class) {
					this.metadata = factory.getInitEntityByClass(initAnnotation.value());
					if (this.metadata == null) {
						log.warn("No meta data found for class " + initAnnotation.value());
					}
				} else {
					String alias = dataAnnotation.annotationType().getSimpleName();
					this.metadata = factory.getInitEntityByAlias(alias);
					if (this.metadata == null) {
						log.warn("No meta data found with alias " + alias);
					}
				}
			}
		}

		@Override
		public String getTypeName() {
			return data.getSimpleName();
		}

	}

	private static class ProcessProperty {
		public ProcessProperty(ProcessEntity entity, InitPropertyMetaData metadata, String name, Object rawValue,
				Object value) {
			this.entity = entity;
			this.metadata = metadata;
			this.name = name;
			this.rawValue = rawValue;
			this.value = value;
		}

		public ProcessEntity entity;
		public InitPropertyMetaData metadata;
		public String name;
		public Object rawValue;
		public Object value;
	}

	public static class PropertiesMap implements Map<String, Object> {

		private final Map<String, ProcessProperty> properties;

		public PropertiesMap(Map<String, ProcessProperty> properties) {
			this.properties = properties;
		}

		@Override
		public int size() {
			return properties.size();
		}

		@Override
		public boolean isEmpty() {
			return properties.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return properties.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public Object get(Object key) {
			ProcessProperty prop = properties.get(key);
			return prop == null ? null : prop.value;
		}

		@Override
		public Object put(String key, Object value) {
			return null;
		}

		@Override
		public Object remove(Object key) {
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> m) {
		}

		@Override
		public void clear() {
		}

		@Override
		public Set<String> keySet() {
			return null;
		}

		@Override
		public Collection<Object> values() {
			return null;
		}

		@Override
		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			return null;
		}

	}

	private static final Logger log = LoggerFactory.getLogger(DefaultInitializer.class);

	private final Map<String, Object> anchores = new HashMap<>();
	private final ArrayStack stack = new ArrayStack();
	private final InitListener listener;
	private boolean insideLoad;
	private final InitializerFactory factory;
	private final SimpleContext context = new SimpleContext();

	public DefaultInitializer(InitializerFactory factory, InitListener listener, Map<String, Object> contextVars) {
		Validate.notNull(factory);

		this.factory = factory;
		this.listener = listener == null ? new BaseInitListener() : listener;

		putInContext("parents", stack);
		putInContext("anchors", anchores);
		if (contextVars != null) {
			for (Entry<String, Object> entry : contextVars.entrySet()) {
				putInContext(entry.getKey(), entry.getValue());
			}
		}
	}

	private void putInContext(String name, Object obj) {
		ExpressionFactory expressionFactory = factory.getExpressionFactory();
		if (expressionFactory != null) {
			context.setVariable(name, expressionFactory.createValueExpression(obj, Object.class));
		}

	}

	@Override
	public Map<String, List<Object>> read(String resourceName) throws Exception {
		return read(Input.resource(resourceName), true);
	}

	@Override
	public Map<String, List<Object>> read(InputStream input, boolean autoClose) throws Exception {
		Validate.notNull(input);

		try {
			SAXBuilder builder = new SAXBuilder();
			Element root = builder.build(input).getRootElement();
			return read(root);
		} finally {
			if (autoClose) {
				input.close();
			}
		}
	}

	@Override
	public Map<String, List<Object>> read(Element root) throws Exception {
		Validate.notNull(root);

		Map<String, List<Object>> result = new HashMap<>();
		for (Element childElem : root.getChildren()) {
			List<Object> list = result.get(childElem.getName());
			if (list == null) {
				list = new ArrayList<>();
				result.put(childElem.getName(), list);
			}
			XmlProcessEntity entity = new XmlProcessEntity(childElem);
			processEntity(entity, list);
		}
		return result;
	}

	/**
	 * Processes the given element. Since an element can be a class-instance or property of its parent, parentEntity
	 * should be passed. In case of no parent, null is an acceptable value.
	 * 
	 * @param parentEntity
	 *            null if no parent is present.
	 * @param entity
	 *            element to be processed
	 * @return
	 */
	protected Object processEntity(ProcessEntity entity, List<Object> parentAsList) {
		String name = entity.getEntityName();
		try {
			// Process special elements: include, load.
			if (INCLUDE_ELEMENT.equals(name)) {
				String srcName = String.valueOf(entity.getAttributeValue("src"));
				// TODO a better resource loading is required like velocity
				if (srcName != null) {
					read(Input.resource(srcName), true);
				}
				return null;
			} else if (LOAD_ELEMENT.equals(name)) {
				// Support nested load elements
				boolean oldInsideLoad = insideLoad;
				insideLoad = true;
				try {
					processChildren(entity, null);
					return null;
				} finally {
					insideLoad = oldInsideLoad;
				}
			}

			// child anchors are previously processed.
			if (entity.parent != null && entity.getAttributeValue(_CHILD_ANCHOR) != null) {
				return null;
			}

			// check whether it is a class alias or a property of an entity
			// initEntiy is null when element is not a class alias

			Object entityObj = getObject(entity);
			entity.value = entityObj;

			if (insideLoad) {
				processAttributes(entity);
				return null;
			}

			// If path element
			if (entityObj == null) {
				processChildren(entity, parentAsList);
				return null;
			}
			// If first level element or inside path element
			if (parentAsList != null) {
				parentAsList.add(entityObj);
			}

			stack.push(entityObj);
			putInContext("entity", entity);
			putInContext("this", new PropertiesMap(entity.properties));
			try {
				processAttributes(entity);
				if (entity.metadata != null) {
					listener.entityCreated(entity.metadata, entityObj);
				}
				processChildren(entity, null);
			} finally {
				stack.pop();
			}

			if (entity.metadata != null) {
				listener.entityFinished(entity.metadata, entityObj);
			}

			return entityObj;
		} catch (Exception e) {
			log.error("Exception occured during processing of element " + name, e);
			return null;
		}
	}

	/**
	 * Creates or finds the object that corresponds to the given element whether it is an instance of a class or
	 * property of an object or first level path element.
	 * 
	 * @param initEntity
	 *            the corresponding initEntityDate definition to the element. It is null when element is not a class
	 *            alias
	 * @return null in case of first level path element.
	 * @throws Exception
	 */
	protected Object getObject(ProcessEntity entity) throws Exception {
		Object entityObj = null;
		if (entity.metadata != null) {
			// class mode
			stack.push(null);
			try {
				entityObj = findOrCreateEntity(entity);
			} finally {
				stack.pop();
			}
		} else if (stack.isEmpty()) {
			// First level path element
			return null;
		} else {
			// property mode
			String propName = entity.getEntityName();

			// check for alias
			if (entity.parent != null && entity.parent.metadata != null) {
				InitPropertyMetaData initProperty = entity.parent.metadata.getProperty(propName);
				if (initProperty != null) {
					propName = initProperty.getName();
				}
			}

			entityObj = PropertyUtils.getProperty(stack.peek(), propName);
			if (entityObj == null) {
				throw new IllegalArgumentException("The property " + entity.getEntityName() + " of object "
						+ stack.peek() + " is null.");
			}
		}
		return entityObj;
	}

	protected void processChildren(ProcessEntity entity, List<Object> parentAsList) {
		for (ProcessEntity childElem : entity.getChildren()) {
			processEntity(childElem, parentAsList);
		}
	}

	protected void processAttributes(ProcessEntity entity) throws Exception {
		computeAllAttributes(entity);

		// Auto anchoring: it creates an anchor like this EntityAlias:key1_key2_key3, provided that all key
		if (factory.isAutoAnchor() && !entity.properties.containsKey(_ANCHOR)) {
			String[] keyProperties = entity.getKeyProperties();
			if (keyProperties != null) {
				StringBuilder uniqeValue = new StringBuilder();
				boolean allNotNull = true;
				for (String keyProperty : keyProperties) {
					Object propertyValue = entity.properties.get(keyProperty).value;
					if (propertyValue == null) {
						allNotNull = false;
						break;
					}
					uniqeValue.append("_").append(propertyValue);
				}
				if (allNotNull) {
					uniqeValue.setCharAt(0, ':'); // convert the first _ to :
					uniqeValue.insert(0, entity.metadata.getAlias());
					entity.properties.put(_ANCHOR,
							new ProcessProperty(entity, null, _ANCHOR, null, uniqeValue.toString()));
				}
			}
		}
		// When data defined using annotations all entities are placed in anchores by their full class name
		if (entity instanceof AnnotationProcessEntity) {
			putObject(((AnnotationProcessEntity) entity).data.getName(), entity.value);
		}
		for (ProcessProperty property : entity.properties.values()) {
			processAttribute(property);
		}
	}

	/**
	 * Creates the entity in non-incremental mode. In incremental mode it first finds the values of all attributes and
	 * then tries to find an entity with values of those attributes that uniquely identifies it. If it fails, the newly
	 * created entity will be returned.
	 */
	protected Object findOrCreateEntity(ProcessEntity entity) throws Exception {
		Object entityObj = entity.metadata.getEntityClass().newInstance();
		if (!factory.isIncremental() && !insideLoad) {
			return entityObj;
		}

		computeAllAttributes(entity);
		String[] properties = entity.getKeyProperties();
		if (properties == null) {
			return entityObj;
		}

		Object[] values = new Object[properties.length];
		for (int i = 0; i < properties.length; i++) {
			values[i] = entity.properties.get(properties[i]).value;
		}

		Object res = listener.findEntity(entity.metadata, properties, values);
		return res == null ? entityObj : res;
	}

	public boolean isReservedAttribute(String name) {
		return _ANCHOR.equals(name) || _ACTION.equals(name) || _IN_PARENT.equals(name) || _CHILD_ANCHOR.equals(name);
	}

	/**
	 * Compute the values of all attributes for this entity whether they are explicitly mentioned in the attributes of
	 * the entity or they have default value in the initEntity.
	 */
	private void computeAllAttributes(ProcessEntity entity) throws Exception {

		for (Entry<String, Object> attr : entity.getAttributes().entrySet()) {
			String attrName = attr.getKey();
			Object attrValue = attr.getValue();
			// resolve aliases
			InitPropertyMetaData prop = null;
			if (entity.metadata != null) {
				prop = entity.metadata.getProperty(attrName);
				if (prop != null) {
					attrName = prop.getName();
				}
			}

			ProcessProperty property = new ProcessProperty(entity, prop, attrName, attrValue, null);
			property.value = getAttributeValue(property);
			entity.properties.put(attrName, property);
		}

		// check for unset defaults
		if (entity.metadata != null) {
			for (InitPropertyMetaData prop : entity.metadata.getProperties()) {
				String attrName = prop.getName();
				if (!entity.properties.containsKey(attrName) && prop.getDefaultValue() != null) {
					ProcessProperty property = new ProcessProperty(entity, prop, attrName, prop.getDefaultValue(), null);
					property.value = getAttributeValue(property);
					entity.properties.put(attrName, property);
				}
			}
		}

	}

	/**
	 * Resolves the value attrValue of attribute attrName to a concrete type within the given context. Note that when
	 * called from inside load, entity is null.
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object getAttributeValue(ProcessProperty property) throws Exception {
		if (property.rawValue == null) {
			return null;
		}

		// Detect the target property type
		Class propertyType = null;
		if (property.entity.value != null && !isReservedAttribute(property.name)) {
			PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(property.entity.value, property.name);
			if (descriptor != null) {
				propertyType = descriptor.getPropertyType();
			} else {
				throw new RuntimeException("No property with name " + property.name + " is found in "
						+ property.entity.value.getClass());
			}
		}

		Object value = property.rawValue;

		// Resolve dynamic values, if allowed
		if ((property.metadata == null || property.metadata.isDynamic())) {
			String valueAsStr = String.valueOf(value);
			if ("null".equals(valueAsStr)) {
				value = null;
			} else if (valueAsStr.startsWith("@")) {
				// check for dynamic references
				value = resolveDynamicReference(property.entity, property.name, valueAsStr, propertyType);
				propertyType = null; // no type conversion is needed anymore
			} else if (value instanceof String && factory.getExpressionFactory() != null) {
				value = factory.getExpressionFactory()
						.createValueExpression(context, valueAsStr, propertyType == null ? Object.class : propertyType)
						.getValue(context);
				propertyType = null; // no type conversion is needed anymore
			}
		}

		if (value != null && propertyType != null) {
			// resolve dynamic reference in annotations
			if (value instanceof Class<?> && !propertyType.isLocalClass()) {
				value = getObject((Class<?>) value);
			}

			if (propertyType.isEnum()) {
				value = Enum.valueOf(propertyType, String.valueOf(value));
			} else {
				value = ConvertUtils.convert(value, propertyType);
			}
		}

		return value;
	}

	protected Object resolveDynamicReference(ProcessEntity entity, String attrName, String attrValue,
			Class<?> propertyType) {
		boolean optional = attrValue.endsWith("?");
		if (optional) {
			attrValue = attrValue.substring(0, attrValue.length() - 1);
		}
		Object value = null;
		if (_ACTION.equals(attrName)) {
			value = attrValue;
		} else if (attrValue.startsWith("@parent(")) {
			int arg = Integer.parseInt(attrValue.substring(8, attrValue.length() - 1));
			try {
				value = stack.peek(arg);
			} catch (Exception e) {
				if (!optional) {
					throw new IllegalArgumentException("Parent index is not valid in stack: " + arg);
				}
			}
		} else if (attrValue.equals("@parent")) {
			// Finds the first parent with specified type
			for (int level = 1; level < stack.size(); level++) {
				Object parent = stack.peek(level);
				if (propertyType != null && propertyType.isAssignableFrom(parent.getClass())) {
					value = parent;
					break;
				}
			}
			if (value == null && !optional) {
				throw new IllegalArgumentException("Could not find an appropriate parent with type " + propertyType
						+ " for property " + attrName + " in " + entity.getEntityName());
			}
		} else if (attrValue.equals("@child")) {
			// now we should look into children to find the one with corresponding child-anchor
			ProcessEntity childEntity = findChild(entity, "child-anchor", attrName);
			if (childEntity == null) {
				throw new IllegalArgumentException("No child with child-anchor = " + attrName + " in "
						+ entity.getEntityName());
			}
			value = processEntity(childEntity, null);
		} else if (attrValue.equals("@type.name")) {
			value = entity.getTypeName();
		} else { // look into anchors
			if (attrValue.indexOf(';') < 0) {
				value = getAnchorValue(attrValue.substring(1), propertyType, optional);
			} else {
				List<Object> list = new ArrayList<Object>();
				for (String part : attrValue.split(";")) {
					// Note that in this case, passing property type is not useful since it is of type List
					list.add(getAnchorValue(part.substring(1), null, optional));
				}
				value = list;
			}

		}
		return value;
	}

	private Object getAnchorValue(String anchorName, Class<?> propertyType, boolean optional) {
		anchorName = getAbsoluteAnchorName(anchorName, propertyType);
		Object value = anchores.get(anchorName);
		if (value != null) {
			return value;
		} else {
			// Auto-scoping: tries to guess
			// First find the alias corresponding to this propertyType
			String alias = null;
			InitEntityMetaData initEntity = factory.getInitEntityByClass(propertyType);
			if (initEntity != null) {
				alias = initEntity.getAlias();
				if (alias != null) {
					value = anchores.get(alias + ":" + anchorName);
					if (value != null) {
						return value;
					}
				}
			}

			if (!optional) {
				throw new IllegalArgumentException("Unknown anchor with name " + anchorName
						+ (alias != null ? " or with name " + alias + ":" + anchorName : ""));
			}
			return null;
		}
	}

	protected void processAttribute(ProcessProperty property) throws Exception {
		String value = String.valueOf(property.value);
		try {
			// check for special attributes
			if (property.name.equals(_ANCHOR)) {
				if (value.startsWith("parent") || value.equals("child") || StringUtils.containsAny(value, '?')) {
					throw new IllegalArgumentException("Illegal anchor name " + value);
				}
				value = getAbsoluteAnchorName(value, property.entity.metadata.getEntityClass());
				if (anchores.containsKey(value)) {
					throw new IllegalArgumentException("Duplicate anchor name " + value);
				}
				anchores.put(value, property.entity.value);
			} else if (property.name.equals(_ACTION)) {
				int index = value.indexOf('.');
				int arg = Integer.parseInt(value.substring(8, index - 1)); // -1 for )
				Object target = stack.peek(arg);
				MethodUtils.invokeMethod(target, value.substring(index + 1), property.entity.value);
			} else if (property.name.equals(_IN_PARENT)) {
				Object parent = stack.size() >= 2 ? stack.get(stack.size() - 2) : null;
				boolean optional = value.endsWith("?");
				if (parent != null) {
					if (optional) {
						value = value.substring(0, value.length() - 1);
					}
					// Check whether the in_parent refers to a collection so we should add instead of set
					PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(parent, value);
					if (List.class.isAssignableFrom(descriptor.getPropertyType())) {
						Object parentProp = PropertyUtils.getProperty(parent, value);
						if (parentProp != null) {
							((List<Object>) parentProp).add(property.entity.value);
						} else {
							// We should create one
							List<Object> list = new ArrayList<>();
							list.add(property.entity.value);
							BeanUtils.setProperty(parent, value, list);
						}
					} else {
						BeanUtils.setProperty(parent, value, property.entity.value);
					}
				} else if (!optional) {
					throw new RuntimeException("non-optional in_parent is specified but the parent is null");
				}
			} else {
				setPropertyValue(property);
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception occured while processing attribute " + property.name + " of element "
					+ property.entity.getEntityName() + ". Entity: " + property.entity.value + ". Attribute value: "
					+ property.value, e);
		}

	}

	protected void setPropertyValue(ProcessProperty property) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		BeanUtils.setProperty(property.entity.value, property.name, property.value);
	}

	protected ProcessEntity findChild(ProcessEntity entity, String attrName, String attrValue) {
		for (ProcessEntity childEntity : entity.getChildren()) {
			if (attrValue.equals(childEntity.getAttributeValue(attrName))) {
				return childEntity;
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> getAnchores() {
		return anchores;
	}

	@Override
	public Object getObject(String anchorName) {
		return getObject(anchorName, Object.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(String anchorName, Class<T> entityClass) {
		Validate.notNull(anchorName);
		return (T) getAnchorValue(anchorName, entityClass, true);
	}

	@Override
	public Object putObject(String anchorName, Object entity) {
		Validate.notNull(anchorName);
		Validate.notNull(entity);

		anchorName = getAbsoluteAnchorName(anchorName, entity.getClass());
		return getAnchores().put(anchorName, entity);
	}

	protected String getAbsoluteAnchorName(String anchorName, Class<?> entityClass) {
		if (anchorName.startsWith(":")) {
			InitEntityMetaData initEntity = factory.getInitEntityByClass(entityClass);
			Validate.notNull(initEntity, "Entity class is not defined " + entityClass);
			anchorName = initEntity.getAlias() + anchorName;
		}
		return anchorName;
	}

	@Override
	public Map<Class<?>, List<Object>> read(Class<?> data) {
		Validate.notNull(data);

		Map<Class<?>, List<Object>> result = new HashMap<>();
		AnnotationProcessEntity processEntity = new AnnotationProcessEntity(data);

		for (AnnotationProcessEntity child : processEntity.getChildren()) {
			// If no metadata treat it as grouping element
			if (child.metadata == null) {
				List<Object> list = result.get(child.data);
				if (list == null) {
					list = new ArrayList<>();
					result.put(child.data, list);
				}
				processChildren(child, list);
			} else {
				List<Object> list = result.get(child.metadata.getEntityClass());
				if (list == null) {
					list = new ArrayList<>();
					result.put(child.metadata.getEntityClass(), list);
				}
				processEntity(child, list);
			}
		}
		return result;
	}

	@Override
	public Object getObject(Class<?> dataClass) {
		Validate.notNull(dataClass);
		return getObject(dataClass.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(Class<?> dataClass, Class<T> entityClass) {
		return (T) getObject(dataClass);
	}

}

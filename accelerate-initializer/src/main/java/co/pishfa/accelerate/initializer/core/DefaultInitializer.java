/**
 * 
 */
package co.pishfa.accelerate.initializer.core;

import co.pishfa.accelerate.initializer.api.InitListener;
import co.pishfa.accelerate.initializer.api.Initializer;
import co.pishfa.accelerate.initializer.api.InitializerFactory;
import co.pishfa.accelerate.initializer.model.InitAnnotation;
import co.pishfa.accelerate.initializer.model.InitEntityMetaData;
import co.pishfa.accelerate.initializer.model.InitPropertyMetaData;
import co.pishfa.accelerate.initializer.util.Input;
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

import javax.el.ExpressionFactory;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class DefaultInitializer implements Initializer {

	/**
	 * Contains information about the current entity under the initialization process.
	 * 
	 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
	 * 
	 */
	private abstract class ProcessEntity {

		public ProcessEntity parent;
		public InitEntityMetaData metadata;
		/**
		 * The current value of this entity. For example, if this entity corresponds to a class, this value is refer to
		 * instance of this class that is created (or loaded) by the initializer.
		 */
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
			String key = StringUtils.defaultIfEmpty(metadata.getKey(), factory.getKey());
			if (PROPERTY_EMPTY.equals(key) || StringUtils.isEmpty(key)) {
				return null;
			}

			if (PROPERTY_ALL.equals(key)) {
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

	/**
	 * An entity that defined in the xml data file.
	 * 
	 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
	 * 
	 */
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

	/**
	 * An entity that defined in an annotation class.
	 * 
	 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
	 * 
	 */
	private class AnnotationProcessEntity extends ProcessEntity {
		/**
		 * The class (which is an annotation class) that defines the data
		 */
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

	/**
	 * Contains information about a property of current entity under process.
	 * 
	 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
	 * 
	 */
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

	/**
	 * Exposes the map of {@link ProcessProperty} as a map of string->object to be used in the el context.
	 * 
	 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
	 * 
	 */
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

		putInContext(CONTEXT_PARENTS, stack);
		putInContext(CONTEXT_ANCHORS, anchores);
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
	 * @param entity
	 *            element to be processed
	 * @return
	 */
	protected Object processEntity(ProcessEntity entity, List<Object> parentAsList) {
		String name = entity.getEntityName();
		try {
			// Process special elements: include, load.
			if (ELEMENT_INCLUDE.equals(name)) {
				String srcName = String.valueOf(entity.getAttributeValue("src"));
				// TODO a better resource loading is required like velocity
				if (srcName != null) {
					read(Input.resource(srcName), true);
				}
				return null;
			} else if (ELEMENT_LOAD.equals(name)) {
				// Support nested load elements
				boolean oldInsideLoad = insideLoad;
				insideLoad = true;
				try {
					processChildren(entity, null);
					return null;
				} finally {
					insideLoad = oldInsideLoad;
				}
			} else if (ELEMENT_VARS.equals(name)) {
				processVars(entity);
				return null;
			}

			// child anchors are previously processed.
			if (entity.parent != null && entity.getAttributeValue(ATTR_CHILD_ANCHOR) != null) {
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
	 * Processes defined variables.
	 */
	private void processVars(ProcessEntity entity) {
		for (ProcessEntity child : entity.getChildren()) {
			String name = (String) child.getAttributeValue("name");
			String value = (String) child.getAttributeValue("value");
			ExpressionFactory expressionFactory = factory.getExpressionFactory();
			context.setVariable(name, expressionFactory.createValueExpression(context, value, Object.class));
		}
	}

	/**
	 * Creates or finds the object that corresponds to the given element whether it is an instance of a class or
	 * property of an object or first level path element.
	 * 
	 * @param entity
	 *            the corresponding initEntityDate definition to the element. It is null when element is not a class
	 *            alias
	 * @return null in case of first level path element.
	 * @throws Exception
	 */
	protected Object getObject(ProcessEntity entity) throws Exception {
		Object entityObj = null;
		if (entity.metadata != null) {
			// class mode
			stack.push(null); // push "this" that is parent(0) which is null right now
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

		// Auto anchoring: it creates an anchor like this EntityAlias:key1_key2_key3, provided that at least one of them
		// has non-null value.
		if (factory.isAutoAnchor() && !entity.properties.containsKey(ATTR_ANCHOR)) {
			String[] keyProperties = entity.getKeyProperties();
			if (keyProperties != null) {
				StringBuilder uniqeValue = new StringBuilder();
				boolean allNotNull = true;
				for (String keyProperty : keyProperties) {
					ProcessProperty property = entity.properties.get(keyProperty);
					Object propertyValue = property == null ? null : property.value;
					if (propertyValue == null) {
						allNotNull = false;
						break;
					}
					uniqeValue.append("_").append(propertyValue);
				}
				if (allNotNull) {
					uniqeValue.setCharAt(0, ':'); // convert the first _ to :
					uniqeValue.insert(0, entity.metadata.getAlias());
					entity.properties.put(ATTR_ANCHOR,
							new ProcessProperty(entity, null, ATTR_ANCHOR, null, uniqeValue.toString()));
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

		String[] properties = entity.getKeyProperties();
		if (properties == null) {
			return entityObj;
		}

		entity.value = entityObj; // just for infering types
		computeAllAttributes(entity);
		Map<String, Object> values = new HashMap<>();
		for (String property : properties) {
			ProcessProperty processProperty = entity.properties.get(property);
			if (processProperty != null) {
				Object value = processProperty.value;
				if (value != null) {
					values.put(property, value);
				}
			}
		}

		if (values.isEmpty())
			return entityObj;

		Object res = listener.findEntity(entity.metadata, values);
		return res == null ? entityObj : res;
	}

	public boolean isReservedAttribute(String name) {
		return ATTR_ANCHOR.equals(name) || ATTR_ACTION.equals(name) || ATTR_IN_PARENT.equals(name)
				|| ATTR_CHILD_ANCHOR.equals(name);
	}

	/**
	 * Compute the values of all attributes for this entity whether they are explicitly mentioned in the attributes of
	 * the entity or they have default value in the initEntity.
	 */
	private void computeAllAttributes(ProcessEntity entity) throws Exception {
		putInContext(CONTEXT_ENTITY, entity);
		putInContext(CONTEXT_THIS, new PropertiesMap(entity.properties));

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
	protected Object getAttributeValue(ProcessProperty property) throws Exception {
		if (property.rawValue == null) {
			return null;
		}

		// Detect the target property type
		Class<?> propertyType = getPropertyType(property.entity.value, property.name);

		if (propertyType != null && List.class.isAssignableFrom(propertyType)) {
			List<Object> list = new ArrayList<Object>();
			// Note that in this case, passing property type is not useful since it is of type List
			Class<?> genericType = null;
			try {
				genericType = (Class<?>) ((ParameterizedType) property.entity.value.getClass()
						.getDeclaredField(property.name).getGenericType()).getActualTypeArguments()[0];
			} catch (Exception e) {
				throw new RuntimeException("Could not determine the generic type of list " + property.name + " in "
						+ property.entity.value.getClass());
			}
			for (String part : String.valueOf(property.rawValue).split(";")) {
				list.add(getAtomicAttributeValue(property, genericType, part));
			}
			return list;
		} else {
			return getAtomicAttributeValue(property, propertyType, property.rawValue);
		}
	}

	private Class<?> getPropertyType(Object entityObj, String name) {
		if (entityObj != null && !isReservedAttribute(name)) {
			try {
				return PropertyUtils.getPropertyType(entityObj, name);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException("No property with name " + name + " is found in " + entityObj.getClass(), e);
			}
			// TODO why bean utils don't provide the property type by class?
			/*PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(entityClass);
			for (PropertyDescriptor descriptor : descriptors)
				if (name.equals(descriptor.getName()))
					return descriptor.getPropertyType();*/
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object getAtomicAttributeValue(ProcessProperty property, Class propertyType, Object value) {
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
		if (ATTR_ACTION.equals(attrName)) {
			value = attrValue;
		} else if (attrValue.startsWith("@parent(")) {
			int arg = Integer.parseInt(attrValue.substring(8, attrValue.length() - 1));
			try {
				Object parent = stack.peek(arg);
				if (!optional) {
					value = parent; // no other choice
				} else if (propertyType != null && propertyType.isAssignableFrom(parent.getClass())) {
					// only assign it, if applies
					value = parent;
				}
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
			value = getAnchorValue(attrValue.substring(1), propertyType, optional);
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

	/**
	 * Processes the attribute after its value is calculated.
	 */
	@SuppressWarnings("unchecked")
	protected void processAttribute(ProcessProperty property) throws Exception {
		String value = String.valueOf(property.value);
		try {
			// check for special attributes
			if (property.name.equals(ATTR_ANCHOR)) {
				if (value.startsWith("parent") || value.equals("child") || StringUtils.containsAny(value, '?')) {
					throw new IllegalArgumentException("Illegal anchor name " + value);
				}
				// empty anchor value, means no anchor. This can be used by entity types to turn off, auto anchoring per
				// entity type.
				if (!StringUtils.isEmpty(value)) {
					value = getAbsoluteAnchorName(value, property.entity.metadata.getEntityClass());
					if (anchores.containsKey(value)) {
						throw new IllegalArgumentException("Duplicate anchor name " + value);
					}
					anchores.put(value, property.entity.value);
				}
			} else if (property.name.equals(ATTR_ACTION)) {
                Object parent = stack.size() >= 2 ? stack.get(stack.size() - 2) : null;
                boolean optional = value.endsWith("?");
                if (parent != null) {
                    if (optional) {
                        value = value.substring(0, value.length() - 1);
                    }
                    MethodUtils.invokeMethod(parent, value, property.entity.value);
                } else if (!optional) {
                    throw new RuntimeException("non-optional in_parent is specified but the parent is null");
                }
			} else if (property.name.equals(ATTR_IN_PARENT)) {
				Object parent = stack.size() >= 2 ? stack.get(stack.size() - 2) : null;
				boolean optional = value.endsWith("?");
				if (parent != null) {
					if (optional) {
						value = value.substring(0, value.length() - 1);
					}
					// Check whether the in_parent refers to a collection so we should add instead of set
					PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(parent, value);
					if (descriptor == null) {
						if (!optional) {
							throw new RuntimeException(
									"non-optional in_parent is specified but the parent does not have property "
											+ value);
						} else {
							return;
						}
					}
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
			Validate.notNull(initEntity, "Entity class is not defined for " + entityClass
					+ " To be used in absolute anchor name");
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

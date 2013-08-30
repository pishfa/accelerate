/**
 * 
 */
package co.pishfa.accelerate.initializer.core;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
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
import co.pishfa.accelerate.initializer.model.InitEntityMetaData;
import co.pishfa.accelerate.initializer.model.InitPropertyMetaData;
import co.pishfa.accelerate.initializer.util.Input;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class DefaultInitializer implements Initializer {

	public static class ProcessEntity {
		public ProcessEntity(ProcessEntity parent, InitEntityMetaData metadata, Element element, Object value) {
			this.parent = parent;
			this.metadata = metadata;
			this.element = element;
			this.value = value;
		}

		public ProcessEntity() {
		}

		public ProcessEntity parent;
		public InitEntityMetaData metadata;
		public Element element;
		public Object value;
		public Map<String, ProcessProperty> properties = new HashMap<>();

		public ProcessEntity getParent() {
			return parent;
		}

		public InitEntityMetaData getMetadata() {
			return metadata;
		}

		public Element getElement() {
			return element;
		}

		public Object getValue() {
			return value;
		}

		public Map<String, ProcessProperty> getProperties() {
			return properties;
		}

	}

	public static class ProcessProperty {
		public ProcessProperty(ProcessEntity entity, InitPropertyMetaData metadata, String name, String rawValue,
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
		public String rawValue;
		public Object value;

		public ProcessEntity getEntity() {
			return entity;
		}

		public InitPropertyMetaData getMetadata() {
			return metadata;
		}

		public String getName() {
			return name;
		}

		public String getRawValue() {
			return rawValue;
		}

		public Object getValue() {
			return value;
		}

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

	// Special elements or attributes
	private static final String LOAD_ELEMENT = "load";
	private static final String INCLUDE_ELEMENT = "include";
	private static final String _IN_PARENT = "_in-parent_";
	private static final String _ACTION = "_action_";
	private static final String _ANCHOR = "_anchor_";
	// TODO is not supported yet
	private static final String _CHILD_ANCHOR = "_child-anchor_";

	private final Map<String, Object> anchores = new HashMap<>();
	private final ArrayStack stack = new ArrayStack();
	private final InitListener listener;
	private boolean insideLoad;
	private final InitializerFactory factory;
	private final SimpleContext context = new SimpleContext();
	private final Map<String, List<Object>> result = new HashMap<>();

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

		for (Element childElem : root.getChildren()) {
			List<Object> list = null;
			list = result.get(childElem.getName());
			if (list == null) {
				list = new ArrayList<>();
				result.put(childElem.getName(), list);
			}
			processElement(null, childElem, list);
		}
		return result;
	}

	/**
	 * Processes the given element. Since an element can be a class-instance or property of its parent, parentEntity
	 * should be passed. In case of no parent, null is an acceptable value.
	 * 
	 * @param parentEntity
	 *            null if no parent is present.
	 * @param element
	 *            element to be processed
	 * @return
	 */
	protected Object processElement(ProcessEntity parent, Element element, List<Object> parentAsList) {
		String name = element.getName();
		try {
			// Process special elements: include, load.
			if (INCLUDE_ELEMENT.equals(name)) {
				String srcName = element.getAttributeValue("src");
				// TODO a better resource loading is required like velocity
				read(Input.resource(srcName), true);
				return null;
			} else if (LOAD_ELEMENT.equals(name)) {
				// Support nested load elements
				boolean oldInsideLoad = insideLoad;
				insideLoad = true;
				try {
					ProcessEntity loadEntity = new ProcessEntity();
					loadEntity.element = element;
					processChildren(loadEntity, null);
					return null;
				} finally {
					insideLoad = oldInsideLoad;
				}
			}

			// child anchors are previously processed.
			if (parent != null && !StringUtils.isEmpty(element.getAttributeValue(_CHILD_ANCHOR))) {
				return null;
			}

			// check whether it is a class alias or a property of an entity
			// initEntiy is null when element is not a class alias
			InitEntityMetaData initEntity = factory.getInitEntityByAlias(name);
			ProcessEntity entity = new ProcessEntity(parent, initEntity, element, null);
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
				if (initEntity != null) {
					listener.entityCreated(initEntity, entityObj);
				}
				processChildren(entity, null);
			} finally {
				stack.pop();
			}

			if (initEntity != null) {
				listener.entityFinished(initEntity, entityObj);
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
	 * @param parentEntity
	 * @param element
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
			String propName = entity.element.getName();

			// check for alias
			if (entity.parent != null) {
				InitPropertyMetaData initProperty = entity.parent.metadata.getPropertiesByAlias().get(propName);
				if (initProperty != null) {
					propName = initProperty.getName();
				}
			}

			entityObj = PropertyUtils.getProperty(stack.peek(), propName);
			if (entityObj == null) {
				throw new IllegalArgumentException("The property " + entity.element.getName() + " of object "
						+ stack.peek() + " is null.");
			}
		}
		return entityObj;
	}

	protected void processChildren(ProcessEntity entity, List<Object> parentAsList) {
		for (Element childElem : entity.element.getChildren()) {
			processElement(entity, childElem, parentAsList);
		}
	}

	protected void processAttributes(ProcessEntity entity) throws Exception {
		getAllAttributes(entity);

		// Auto anchoring: it creates an anchor like this EntityAlias:key1_key2_key3, provided that all key
		if (factory.isAutoAnchor() && !entity.properties.containsKey(_ANCHOR)) {
			String[] keyProperties = getKeyProperties(entity);
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

		getAllAttributes(entity);
		String[] properties = getKeyProperties(entity);
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

	/**
	 * finds the unifying attributes. By default, it is factory.getUniquePropertyName().
	 * 
	 */
	protected String[] getKeyProperties(ProcessEntity entity) {
		String key = StringUtils.defaultIfEmpty(entity.metadata.getKey(), factory.getKeyPropertyName());
		if (StringUtils.isEmpty(key)) {
			return null;
		}

		if ("*".equals(key)) {
			StringBuilder ustr = new StringBuilder();
			for (String name : entity.properties.keySet()) {
				if (!isReservedAttribute(name)) {
					ustr.append(",").append(name);
				}
			}
			key = ustr.deleteCharAt(0).toString();
		}

		String[] properties = key.split(",");
		return properties;
	}

	public boolean isReservedAttribute(String name) {
		return _ANCHOR.equals(name) || _ACTION.equals(name) || _IN_PARENT.equals(name) || _CHILD_ANCHOR.equals(name);
	}

	/**
	 * Get the values of all attributes for this element whether they are explicitly mentioned in the attributes of the
	 * element or they have default value in the initEntity.
	 */
	private void getAllAttributes(ProcessEntity entity) throws Exception {

		for (Attribute attr : entity.element.getAttributes()) {
			String attrName = attr.getName();
			String attrValue = attr.getValue();
			// resolve aliases
			InitPropertyMetaData prop = null;
			if (entity.metadata != null) {
				prop = entity.metadata.getPropertiesByAlias().get(attrName);
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
		if ((property.metadata == null || property.metadata.isDynamic())) {
			if ("null".equals(property.rawValue)) {
				value = null;
			} else if (property.rawValue.startsWith("@")) {
				// check for dynamic references
				value = resolveDynamicReference(property.entity.element, property.name, property.rawValue, propertyType);
				propertyType = null; // no type conversion needed anymore
			} else if (factory.getExpressionFactory() != null) {
				value = factory
						.getExpressionFactory()
						.createValueExpression(context, property.rawValue,
								propertyType == null ? Object.class : propertyType).getValue(context);
				propertyType = null; // no type conversion needed anymore
			}
		}

		if (value != null && propertyType != null) {
			if (propertyType.isEnum()) {
				value = Enum.valueOf(propertyType, property.rawValue.toString());
			} else {
				value = ConvertUtils.convert(property.rawValue, propertyType);
			}
		}
		return value;
	}

	protected Object resolveDynamicReference(Element element, String attrName, String attrValue, Class<?> propertyType) {
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
						+ " for property " + attrName + " in " + element);
			}
		} else if (attrValue.equals("@child")) {
			// now we should look into children to find the one with corresponding child-anchor
			Element childElem = findChild(element, "child-anchor", attrName);
			if (childElem == null) {
				throw new IllegalArgumentException("No child with child-anchor = " + attrName + " in " + element);
			}
			value = processElement(null, childElem, null);
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
				Object parent = stack.get(stack.size() - 2);
				BeanUtils.setProperty(parent, value, property.entity.value);
			} else {
				setPropertyValue(property);
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception occured while processing attribute " + property.name + " of element "
					+ property.entity.element.getName() + ". Entity: " + property.entity.value + ". Attribute value: "
					+ property.value, e);
		}

	}

	protected void setPropertyValue(ProcessProperty property) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		BeanUtils.setProperty(property.entity.value, property.name, property.value);
	}

	protected Element findChild(Element element, String attrName, String attrValue) {
		for (Element childElem : element.getChildren()) {
			if (attrValue.equals(childElem.getAttributeValue(attrName))) {
				return childElem;
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> getAnchores() {
		return anchores;
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
	public Map<String, List<Object>> read(Class<?> data) {
		return null;
	}

	@Override
	public <T> T getObject(Class<?> dataClass, Class<T> entityClass) {
		return null;
	}

}

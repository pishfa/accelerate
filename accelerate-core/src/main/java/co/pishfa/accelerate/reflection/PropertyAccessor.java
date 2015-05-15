/**
 * 
 */
package co.pishfa.accelerate.reflection;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.pishfa.accelerate.utility.CommonUtils;


/**
 * @author Taha Ghasemi
 * 
 */
public class PropertyAccessor<T> {

	private static final Logger log = LoggerFactory.getLogger(PropertyAccessor.class);

	private final String propertyName;
	private final Object entity;

	public PropertyAccessor(String propertyName, Object entity) {
		super();
		this.propertyName = propertyName;
		this.entity = entity;
	}

	public T getTarget() {
		try {
			return CommonUtils.cast(PropertyUtils.getSimpleProperty(entity, propertyName));
		} catch (Exception e) {
			log.error("", e);
			return null;
		}
	}

	public void setTarget(T obj) {
		try {
			PropertyUtils.setSimpleProperty(entity, propertyName, obj);
		} catch (Exception e) {
			log.error("", e);
		}
	}

}

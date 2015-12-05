/**
 * 
 */
package co.pishfa.accelerate.clone;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * A stateless custom clonner.
 * 
 * @author Taha Ghasemi
 * 
 */
public interface CustomClonner {

	/**
	 * Clone the given field of object. Current value of the field on this object is passed by fieldValue parameter. For the same fieldValue
	 * appeared in different objects, this function will be called each time, so it must handle this case, internally if required.
	 * 
	 * @return the cloned value.
	 */
	Object clone(Cloner cloner, Map<Object, Object> clones, Field field, Object object, Object fieldValue) throws Exception;

}

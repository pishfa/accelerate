/**
 * 
 */
package co.pishfa.accelerate.clone;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom cloning behavior
 * 
 * @author Taha Ghasemi
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CloneCustom {

	public Class<? extends CustomClonner> value();

}

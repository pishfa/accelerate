package co.pishfa.accelerate.initializer.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines attributes of data-defining annotation
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface InitAnnotation {

	/**
	 * 
	 * @return the target entity class that the target annotation defined data for it.
	 */
	Class<?> value() default Object.class;

}

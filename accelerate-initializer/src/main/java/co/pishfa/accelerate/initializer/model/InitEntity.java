/**
 * 
 */
package co.pishfa.accelerate.initializer.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.pishfa.accelerate.initializer.core.DefaultInitializer;

/**
 * Configures the annotated type for initialization process to be used in {@link DefaultInitializer}.
 * 
 * @author Taha Ghasemi
 * 
 */
@Target({ ElementType.TYPE })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface InitEntity {

	/**
	 * @return additional properties for this entity. Can be used to override properties of parent or properties without
	 *         a field
	 */
	InitProperty[] properties() default {};

	/**
	 * @return the entity alias name which will be used in the xml to refer to that entity.. If not specified, the class
	 *         simple name will be used as the alias.
	 */
	String alias() default "";

}

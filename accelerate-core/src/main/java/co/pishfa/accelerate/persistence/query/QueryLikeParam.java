/**
 * 
 */
package co.pishfa.accelerate.persistence.query;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the parameter should be treated as like parameter by adding % character at the beginning and/or at the
 * end of the parameter. This should be placed on the parameters of a method that annotated with {@link QueryRunner}.
 * 
 * @author Taha Ghasemi
 * 
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryLikeParam {

	/**
	 * 
	 * @return should the start be appended with %. (defaults to true)
	 */
	boolean begin() default true;

	/**
	 * 
	 * @return should the end be appended with %. (defaults to true)
	 */
	boolean end() default true;

	/**
	 * 
	 * @return to set parameter by name instead of its position
	 */
	String name() default "";

}

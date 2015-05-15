/**
 * 
 */
package co.pishfa.accelerate.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configures the annotated type as a set of configuration items. Fields of this type can be annotated with
 * {@link co.pishfa.accelerate.config.ConfigProperty}. The name of configuration item is determined by
 * alias of this entity + (field name or config property alias). The value of this annotation (the entity alias)
 * is used for both getting and setting the corresponding object into the configuration system, though it can be
 * overridden explicitly. This annotation can be used for declaring a common namespace (alias prefix) for all inner
 * properties or config entities.
 * 
 * @author Taha Ghasemi
 * 
 */
@Target({ ElementType.TYPE })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigEntity {

	/**
	 * @return The alias for this entity.
	 */
	public String value() default "";

}

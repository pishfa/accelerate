/**
 * 
 */
package co.pishfa.accelerate.config;

import java.lang.annotation.*;

/**
 * Specifies one property of a {@link co.pishfa.accelerate.config.ConfigEntity}.
 * 
 * @author Taha Ghasemi
 * 
 */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {

	/**
	 * @return The alias for this property. By default it is the name of field. The final key is constructed using the
     * enclosing config entity alias + this property alias.
	 */
	public String value() default "";

    /**
     * @return true if this property should be ignored in the configuration processing.
     */
    public boolean ignore() default false;

}

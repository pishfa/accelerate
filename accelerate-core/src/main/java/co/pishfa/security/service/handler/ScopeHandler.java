package co.pishfa.security.service.handler;

import java.lang.annotation.*;

/**
 * Indicates that this class is responsible for scopes of certain name.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target({ ElementType.TYPE })
public @interface ScopeHandler {

	/**
	 * 
	 * @return the name of scope this handler can be applied to
	 */
	String value();

}

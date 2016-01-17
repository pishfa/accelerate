/**
 * 
 */
package co.pishfa.security;

import co.pishfa.security.entity.audit.AuditLevel;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * @author Taha Ghasemi
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
@InterceptorBinding
@Inherited
public @interface Audited {

	@Nonbinding
	String action() default "";

	@Nonbinding
	AuditLevel level() default AuditLevel.INFO;

    /**
     *
     * @return when true, only the root action will be audited and any subsequent calls inside this action (provided that they all in the same thread) will not be audited.
     */
    @Nonbinding
    boolean onlyRoot() default true;

}

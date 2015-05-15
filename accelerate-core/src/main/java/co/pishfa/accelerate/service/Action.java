/**
 * 
 */
package co.pishfa.accelerate.service;

import co.pishfa.accelerate.log.Logged;
import co.pishfa.accelerate.validation.AutoValidating;
import co.pishfa.security.Audited;
import co.pishfa.security.Secured;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * @author Taha Ghasemi
 * 
 */
@Logged
@Secured
@AutoValidating
@Audited
@Transactional
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@InterceptorBinding
@Inherited
public @interface Action {

    /**
     * The action name.
     */
    @Nonbinding
    public String value() default "";


}

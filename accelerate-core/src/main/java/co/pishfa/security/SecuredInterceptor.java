/**
 * 
 */
package co.pishfa.security;

import co.pishfa.accelerate.service.Action;
import co.pishfa.accelerate.service.EntityService;
import co.pishfa.accelerate.meta.domain.EntityMetadataService;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.reflection.ReflectionUtils;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.entity.authentication.Identity;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * @author Taha Ghasemi
 * 
 */
@Secured
@Interceptor
public class SecuredInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Identity identity;

	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		Secured secured = ReflectionUtils.getAnnotation(ic.getMethod(), Secured.class);
		if (secured == null) {
			return ic.proceed();
		}

		Object target = null;
		if (ic.getParameters().length > 0) {
			target = ic.getParameters()[0];
		}
		String action = secured.value();
		if (StrUtils.isEmpty(action)) {
            Action actionStereotype = ReflectionUtils.getAnnotation(ic.getMethod(), Action.class);
            if(actionStereotype != null && !StrUtils.isEmpty(actionStereotype.value())) {
                action = actionStereotype.value();
            } else {
                action = ic.getMethod().getName();
                if (ic.getTarget() instanceof EntityService<?, ?>) {
                    action = ((EntityService<?, ?>) ic.getTarget()).getEntityMetadata().getAction(action);
                } else if (target != null && target instanceof Entity) {
                    action = EntityMetadataService.getInstance().getEntityMetadata(((Entity) target).getClass(), Long.class)
                            .getAction(action);
                }
            }
		}
		identity.checkPermission(target, action);
		Object result = ic.proceed();
		return result;
	}
}

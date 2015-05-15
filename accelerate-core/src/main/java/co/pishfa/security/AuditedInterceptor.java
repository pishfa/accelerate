/**
 * 
 */
package co.pishfa.security;

import co.pishfa.accelerate.service.Action;
import co.pishfa.accelerate.service.BaseEntityService;
import co.pishfa.accelerate.meta.domain.EntityMetadataService;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.reflection.ReflectionUtils;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.service.AuditService;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * @author Taha Ghasemi
 * 
 */
@Audited
@Interceptor
public class AuditedInterceptor implements Serializable {

	@Inject
	private AuditService auditService;

	private static final long serialVersionUID = 1L;

	public AuditedInterceptor() {
	}

	@AroundInvoke
	public Object aroundInvoke(final InvocationContext ic) throws Exception {
        Audited audited = ReflectionUtils.getAnnotation(ic.getMethod(), Audited.class);
        boolean set = false;
        if(audited.onlyRoot() && !auditService.isRootAudited()) {
            auditService.setRootAudited(true);
            set = true;
        }

        try {
            Object result = ic.proceed();

            if(set || !audited.onlyRoot()) {
                Object target = null;
                if (ic.getParameters().length > 0) {
                    target = ic.getParameters()[0];
                }
                String action = audited.action();
                if (StrUtils.isEmpty(action)) {
                    Action actionStereotype = ReflectionUtils.getAnnotation(ic.getMethod(), Action.class);
                    if(actionStereotype != null && !StrUtils.isEmpty(actionStereotype.value())) {
                        action = actionStereotype.value();
                    } else {
                        action = ic.getMethod().getName();
                        if (ic.getTarget() instanceof BaseEntityService) {
                            action = ((BaseEntityService<?, Long>) ic.getTarget()).getRepository().getEntityMetadata()
                                    .getAction(action);
                        } else if (target != null && target instanceof Entity) {
                            action = EntityMetadataService.getInstance().getEntityMetadata(((Entity) target).getClass(), Long.class)
                                    .getAction(action);
                        }
                    }
                }
                auditService.audit(target, action, null, audited.level());
            }
            return result;
        } finally {
            if(set)
                auditService.removeRootAudited();
        }
    }
}

/**
 * 
 */
package co.pishfa.security.service;

import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.repo.UserRepo;
import co.pishfa.security.entity.authentication.User;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@RunAs
@Interceptor
public class RunAsInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@AroundInvoke
	public Object aroundInvoke(final InvocationContext ic) throws Exception {
		RunAs runAs = ic.getMethod().getAnnotation(RunAs.class);
		User user = null;
		if (!StrUtils.isEmpty(runAs.username())) {
			user = UserRepo.getInstance().findByName(runAs.username());
		}
		return new RunAsWork() {
			@Override
			protected Object work() throws Exception {
				return ic.proceed();
			}
		}.run(user, runAs.systemMode(), runAs.actionName());
	}
}

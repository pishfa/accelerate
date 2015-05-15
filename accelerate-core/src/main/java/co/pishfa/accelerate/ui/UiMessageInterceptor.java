/**
 * 
 */
package co.pishfa.accelerate.ui;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import co.pishfa.accelerate.message.UserMessages;
import co.pishfa.accelerate.utility.StrUtils;

/**
 * @author Taha Ghasemi
 * 
 */
@UiMessage
@Interceptor
public class UiMessageInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserMessages userMessages;

	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		Object res = ic.proceed();
		UiMessage uiMessage = ic.getMethod().getAnnotation(UiMessage.class);
		String key = uiMessage.value();
		if (StrUtils.isEmpty(key)) {
			key = "controller." + ic.getMethod().getName();
		}
		userMessages.add(uiMessage.severity(), key, ic.getParameters());
		if (res != null) {
			userMessages.keepMessages();
		}
		return res;
	}
}

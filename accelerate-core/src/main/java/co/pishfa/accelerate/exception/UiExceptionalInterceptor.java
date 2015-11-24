package co.pishfa.accelerate.exception;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolationException;

import co.pishfa.accelerate.exception.ExceptionService.UiExceptionData;
import co.pishfa.accelerate.message.UserMessages;
import co.pishfa.accelerate.utility.StrUtils;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@UiExceptional
@Interceptor
public class UiExceptionalInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ExceptionService exceptionService;

	@Inject
	private UserMessages userMessages;

	@AroundInvoke
	public Object invoke(InvocationContext ic) throws Exception {
		try {
			return ic.proceed();
		} catch (Exception e) {
			UiExceptionData uiException = exceptionService.getUiException(e);
			if (uiException != null) {
				if (!StrUtils.isEmpty(uiException.message)) {
					userMessages.error(uiException.message);
				}
				if (!StrUtils.isEmpty(uiException.page)) {
					// This is not required if all pages are in ac: scope, but since custom pages might be provided we
					// added it here too
					userMessages.keepMessages();
					return uiException.page;
				} else {
					return null;
				}
			} else {
				throw e;
			}
		}
	}

}

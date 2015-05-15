/**
 * 
 */
package co.pishfa.accelerate.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import co.pishfa.accelerate.message.UserMessage;
import co.pishfa.accelerate.message.UserMessageSeverity;
import co.pishfa.accelerate.message.UserMessages;

/**
 * @author Taha Ghasemi
 * 
 */
@UiValidator
@Interceptor
public class UiValidatorInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserMessages userMessages;

	@AroundInvoke
	public Object aroundInvoke(InvocationContext ctx) throws Exception {
		try {
			Object result = ctx.proceed();
			if (result instanceof String) {
				throw new ValidatorException(userMessages.create(UserMessageSeverity.ERROR, (String) result));
			} else if (result instanceof UserMessage) {
				throw new ValidatorException(userMessages.create((UserMessage) result));
			}
			return null;
		} catch (ValidationException e) {
			List<FacesMessage> messages = new ArrayList<>();
			for (UserMessage message : e.getMessages()) {
				messages.add(userMessages.create(message));
			}
			throw new ValidatorException(messages);
		}
	}

}

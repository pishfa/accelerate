/**
 * 
 */
package co.pishfa.accelerate.validation;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.Validator;
import java.io.Serializable;

/**
 * @author Taha Ghasemi
 * 
 */
@AutoValidating
@Interceptor
public class AutoValidatingInterceptor implements Serializable {

	@Inject
	private Validator validator;

	private static final long serialVersionUID = 1L;

	@AroundInvoke
	public Object aroundInvoke(InvocationContext ctx) throws Exception {
        Object result = ctx.proceed();
        /* TODO
		Set<MethodConstraintViolation<Object>> violations = validator.unwrap(MethodValidator.class)
				.validateAllParameters(ctx.getTarget(), ctx.getMethod(), ctx.getParameters());

		if (!violations.isEmpty()) {
			throw new MethodConstraintViolationException(getMessage(ctx.getMethod(), ctx.getParameters(), violations),
					violations);
		}

		Object result = ctx.proceed();

		violations = validator.unwrap(MethodValidator.class).validateReturnValue(ctx.getTarget(), ctx.getMethod(),
				result);

		if (!violations.isEmpty()) {
			throw new MethodConstraintViolationException(getMessage(ctx.getMethod(), ctx.getParameters(), violations),
					violations);
		}
        */
		return result;
	}

	/*private String getMessage(Method method, Object[] args, Set<? extends MethodConstraintViolation<?>> violations) {

		StringBuilder message = new StringBuilder();
		message.append(violations.size());
		message.append(" constraint violation(s) occurred during method invocation.");
		message.append("\nMethod: ");
		message.append(method);
		message.append("\nArgument values: ");
		message.append(Arrays.toString(args));
		message.append("\nConstraint violations: ");

		int i = 1;
		for (MethodConstraintViolation<?> oneConstraintViolation : violations) {
			message.append("\n (");
			message.append(i);
			message.append(") Kind: ");
			message.append(oneConstraintViolation.getKind());
			message.append("\n parameter index: ");
			message.append(oneConstraintViolation.getParameterIndex());
			message.append("\n message: ");
			message.append(oneConstraintViolation.getMessage());
			message.append("\n root bean: ");
			message.append(oneConstraintViolation.getRootBean());
			message.append("\n property path: ");
			message.append(oneConstraintViolation.getPropertyPath());
			message.append("\n constraint: ");
			message.append(oneConstraintViolation.getConstraintDescriptor().getAnnotation());

			i++;
		}

		return message.toString();
	}*/

}

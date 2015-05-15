package co.pishfa.security.exception;

import co.pishfa.accelerate.exception.UiException;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@UiException(message = "pages.error.authorize", page = "ac:error")
public class AuthorizationException extends SecurityException {

	private static final long serialVersionUID = 1L;

	private Object target;
	private String action;

	public AuthorizationException(Object target, String action, String message) {
		super(message);
		this.target = target;
		this.action = action;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}

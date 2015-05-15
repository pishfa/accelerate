package co.pishfa.security.exception;

import co.pishfa.accelerate.exception.UiException;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@UiException(message = "security.error.relogin")
public class ReLoginException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

}

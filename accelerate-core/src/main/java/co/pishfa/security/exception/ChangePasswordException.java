package co.pishfa.security.exception;

import co.pishfa.accelerate.exception.UiException;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@UiException(page = "ac:passwordChange", message = "security.error.changePassword")
public class ChangePasswordException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

}

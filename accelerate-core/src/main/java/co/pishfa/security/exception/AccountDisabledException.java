package co.pishfa.security.exception;

import co.pishfa.accelerate.exception.UiException;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@UiException(message = "security.error.disabled-account")
public class AccountDisabledException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

}

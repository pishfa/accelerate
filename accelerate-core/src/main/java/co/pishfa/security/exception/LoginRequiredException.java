package co.pishfa.security.exception;

import co.pishfa.accelerate.exception.UiException;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@UiException(page = "ac:login")
public class LoginRequiredException extends SecurityException {

	private static final long serialVersionUID = 1L;

	public LoginRequiredException() {
		super("");
	}

}

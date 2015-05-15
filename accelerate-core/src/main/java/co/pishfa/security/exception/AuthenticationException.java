package co.pishfa.security.exception;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class AuthenticationException extends SecurityException {

	private static final long serialVersionUID = 1L;

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException() {
		super(null);
	}

}

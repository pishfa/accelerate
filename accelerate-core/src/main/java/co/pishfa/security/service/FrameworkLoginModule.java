/**
 * 
 */
package co.pishfa.security.service;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Taha Ghasemi
 * 
 */
public class FrameworkLoginModule implements LoginModule {

	private static final Logger log = LoggerFactory.getLogger(FrameworkLoginModule.class);

	protected Subject subject;
	protected Map<String, ?> options;
	protected CallbackHandler callbackHandler;
	protected String username;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
		this.subject = subject;
		this.options = options;
		this.callbackHandler = callbackHandler;
	}

	@Override
	public boolean login() throws LoginException {
		try {
			NameCallback cbName = new NameCallback("Enter username");
			PasswordCallback cbPassword = new PasswordCallback("Enter password", false);

			// Get the username and password from the callback handler
			callbackHandler.handle(new Callback[] { cbName, cbPassword });
			username = cbName.getName();
		} catch (Exception ex) {
			log.warn("Error logging in", ex);
			LoginException le = new LoginException(ex.getMessage());
			le.initCause(ex);
			throw le;
		}
		System.out.println("---------------------------- Authenticating " + username);
		return false;
	}

	@Override
	public boolean commit() throws LoginException {
		subject.getPrincipals().add(new SimplePrincipal(username));
		return true;
	}

	@Override
	public boolean abort() throws LoginException {
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		return true;
	}

}

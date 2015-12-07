package co.pishfa.security.service;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.validation.ValidationException;
import co.pishfa.security.LoggedOutEvent;
import co.pishfa.security.SecurityUtils;
import co.pishfa.security.exception.AuthenticationException;
import co.pishfa.security.entity.authentication.Domain;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authentication.SecurityPolicy;
import co.pishfa.security.entity.authentication.User;
import co.pishfa.security.exception.ChangePasswordException;
import co.pishfa.security.repo.UserRepo;
import org.apache.commons.lang3.RandomStringUtils;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Service
public class AuthenticationService implements Serializable {

    private static final long serialVersionUID = 1L;

	@Inject
	private Event<LoggedOutEvent> loggedOutEvent;

	@Inject
	private Identity identity;

	@Inject
	private UserRepo userRepo;

	@Inject
	private Authenticator authenticator;

	public static AuthenticationService getInstance() {
		return CdiUtils.getInstance(AuthenticationService.class);
	}

	public void login(final String username, final String password) throws AuthenticationException {
		authenticator.authenticate(username, password);
	}

	public void logout() {
		// Most of work is done is OnlineUserSerivce
		loggedOutEvent.fire(new LoggedOutEvent(getCurrentUser()));
	}

	@Produces
	@Named("user")
	@RequestScoped
	public User getCurrentUser() {
		return identity.getUser();
	}

	public User getCurrentUserAttached() {
		return userRepo.findById(getCurrentUser().getId());
	}

	public String hashPassword(final String pass) {
		return SecurityUtils.hash(pass);
	}

	public String hashSecurityAnswer(final String answer) {
		return SecurityUtils.hash(answer);
	}

	/**
	 * @return the current site secDomain (this is for multi-sites applications)
	 */
	@Produces
	@RequestScoped
	public Domain getCurrentDomain() {
		return getCurrentUser().getDomain();
	}

	@Produces
	@RequestScoped
	public SecurityPolicy getCurrentSecurityPolicy() {
		return getCurrentDomain().getSecurityPolicyInherited();
	}

	/**
	 * Validates the given password against the rules dictated by the current security policy.
	 * 
	 */
	public void validatePassword(final String username, final String password) throws ValidationException, IOException {
		getCurrentSecurityPolicy().validatePassword(username, password);
	}

	public String generateRandomUsername() {
		String username;
		do {
			username = "u" + RandomStringUtils.randomNumeric(8);
		} while (userRepo.checkByName(username));
		return username;
	}

	public String generateRandomPassword() {
		return "p" + RandomStringUtils.randomAlphanumeric(8);
	}

	public String generateValidatablePassword() {
		return getCurrentSecurityPolicy().generateValidatablePassword();
	}

}

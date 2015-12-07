package co.pishfa.security.service;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.context.SessionContext;
import co.pishfa.accelerate.storage.service.DefaultFileService;
import co.pishfa.accelerate.ui.UiService;
import co.pishfa.security.LoggedInEvent;
import co.pishfa.security.entity.audit.AuditLevel;
import co.pishfa.security.entity.authentication.SecurityPolicy;
import co.pishfa.security.entity.authentication.SecurityPolicy.LoginFailAction;
import co.pishfa.security.entity.authentication.User;
import co.pishfa.security.exception.*;
import co.pishfa.security.repo.UserRepo;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@ApplicationScoped
public class Authenticator {

	public static Authenticator getInstance() {
		return CdiUtils.getInstance(Authenticator.class);
	}

	@Inject
	protected Logger log;

	@Inject
	protected UserRepo userRepo;

	@Inject
	protected AuditService auditService;

	@Inject
	protected AuthenticationService authenticationService;

	@Inject
	protected Event<LoggedInEvent> userLoggedInEvent;

	@Inject
	protected SecurityConfig securityConfig;

	@Inject
	protected SessionContext sessionContext;

    @Inject
    private UiService uiService;

	@Inject
	private OnlineUserService onlineUserService;

	public void authenticate(String username, String password) throws AuthenticationException {
		User user = null;
		SecurityPolicy policy = null;
		try {
			// TODO Avoid session fixation
			/*
			 * FacesContext fCtx = FacesContext.getCurrentInstance();
			 * HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
			 * session.invalidate();
			 * fCtx.getExternalContext().getSession(true);
			 */

			user = userRepo.findByName(username);
			policy = user.getDomain().getSecurityPolicyInherited();
			if (securityConfig.isSecurityEnabled()) {
				checkAllowedToLogin(user, policy);
				if (!securityConfig.isSsoEnabled()
						|| "true".equals(sessionContext.get(SecurityConstants.SESSION_LOCAL_LOGIN, String.class))) {
					password = authenticationService.hashPassword(password);
					if (!password.equals(user.getLoginInfo().getPasswordHash())) {
						throw new WrongPasswordException();
					}
				}
			}
			successfulLogin(user, policy);
		} catch (AuthenticationException e) {
			if (user != null) {
				unsuccessfulLogin(user, policy);
			}
			throw e;
		} catch (NoResultException nre) {
			throw new NoUserException();
		} finally {
			if (user != null) {
				user.getLoginInfo().setLastLoginTime(new Date());
				user.getLoginInfo().setLastUrl(uiService.getRequest().getRemoteAddr());
				userRepo.edit(user);
				if (user.needToChangePassword()) {
					throw new ChangePasswordException();
				}
			}
		}
	}

	public void unsuccessfulLogin(User user, SecurityPolicy policy) {
		int attempts = user.getLoginInfo().getLoginAttempts();
		LoginFailAction failAction = policy.getLoginFailAction();
		if (attempts > policy.getNumberOfFailedTries() && failAction != LoginFailAction.NOTHING) {
			if (failAction == LoginFailAction.DISABLE_ACCOUNT) {
				user.getActivation().setEnabled(false);
				user.getLoginInfo().setLoginAttempts(0); // reset it
			}
			auditService.audit(user, SecurityConstants.ACTION_USER_LOGIN_MORE_THAN_ALLOWED, null, AuditLevel.WARN);
		} else {
			user.getLoginInfo().setLoginAttempts(attempts + 1);
		}
	}

	protected void successfulLogin(User user, SecurityPolicy policy) {
		user.getLoginInfo().setLoginAttempts(0); // reset it
		uiService.getSession().setMaxInactiveInterval(policy.getSessionTimeout());
        //cache user image while it is attached
        DefaultFileService.getInstance().getUrl(user.getImage());
		userLoggedInEvent.fire(new LoggedInEvent(user));
	}

	public void checkAllowedToLogin(User user, SecurityPolicy policy) {
		if (!user.isActive()) {
			throw new AccountDisabledException();
		}

		if (policy.isPreventMultipleLogin() && onlineUserService.isOnline(user)) {
			throw new ReLoginException();
		}

		if (policy.getLoginFailAction() == LoginFailAction.DISABLE_LIMITED_TIME
				&& user.getLoginInfo().getLoginAttempts() > policy.getNumberOfFailedTries()) {
			// check for wait time is passed or not
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, -policy.getWaitTimeForRelogin());
			if (calendar.getTime().after(user.getLoginInfo().getLastLoginTime())) {
				user.getLoginInfo().setLoginAttempts(0); // reset it
			} else {
				throw new AccountDisabledException();
			}
		}
	}
}

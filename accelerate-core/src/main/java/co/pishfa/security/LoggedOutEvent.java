/**
 * 
 */
package co.pishfa.security;

import co.pishfa.accelerate.common.AuditableEvent;
import co.pishfa.security.service.AuditService;
import co.pishfa.security.service.SecurityConstants;
import co.pishfa.security.entity.audit.AuditLevel;
import co.pishfa.security.entity.authentication.User;

/**
 * @author Taha Ghasemi
 * 
 */
public class LoggedOutEvent implements AuditableEvent {

	private final User user;

	/**
	 * @param user
	 */
	public LoggedOutEvent(final User user) {
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	@Override
	public void audit() {
		AuditService.getInstance().audit(user, SecurityConstants.ACTION_USER_LOGOUT, null, AuditLevel.INFO);
	}
}

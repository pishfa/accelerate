/**
 * 
 */
package co.pishfa.security;

import co.pishfa.accelerate.common.AuditableEvent;
import co.pishfa.security.entity.authentication.OnlineUser;
import co.pishfa.security.service.AuditService;
import co.pishfa.security.service.SecurityConstants;
import co.pishfa.security.entity.audit.AuditLevel;

/**
 * @author Taha Ghasemi
 * 
 */
public class LoggedOutEvent implements AuditableEvent {

	private final OnlineUser onlineUser;

	public LoggedOutEvent(final OnlineUser onlineUser) {
		this.onlineUser = onlineUser;
	}

	/**
	 * @return the user
	 */
	public OnlineUser getOnlineUser() {
		return onlineUser;
	}

	@Override
	public void audit() {
		AuditService.getInstance().audit(onlineUser.getUser(), SecurityConstants.ACTION_USER_LOGOUT, null, AuditLevel.INFO);
	}
}

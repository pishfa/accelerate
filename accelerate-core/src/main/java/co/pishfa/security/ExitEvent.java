/**
 * 
 */
package co.pishfa.security;

import co.pishfa.accelerate.common.AuditableEvent;
import co.pishfa.security.entity.audit.AuditLevel;
import co.pishfa.security.entity.authentication.OnlineUser;
import co.pishfa.security.service.AuditService;
import co.pishfa.security.service.SecurityConstants;

/**
 * Event either due to explicit logged out or session expiration
 * @author Taha Ghasemi
 * 
 */
public class ExitEvent implements AuditableEvent {

	private final OnlineUser onlineUser;

	public ExitEvent(final OnlineUser onlineUser) {
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
	}
}

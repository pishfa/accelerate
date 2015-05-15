/**
 * 
 */
package co.pishfa.security.service;

import co.pishfa.security.entity.audit.AuditLevel;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authentication.User;

/**
 * @author Taha Ghasemi
 * 
 */
public abstract class RunAsWork {

	public Object run(User user, boolean systemMode, String actionName) throws Exception {
		Identity identity = Identity.getInstance();
		User oldUser = null;
        boolean oldSystemMode = identity.isSystemMode();
		try {
			if (actionName != null) {
				String message = user == null ? "" : user.getName();
				AuditService.getInstance().audit(actionName, SecurityConstants.ACTION_RUN_AS,
						message + "@systemMode=" + systemMode, AuditLevel.INFO);
			}
			if (user != null) {
				oldUser = identity.getUser();
				identity.setUser(user);
			}
			identity.setSystemMode(systemMode);
			return work();
		} finally {
			identity.setSystemMode(oldSystemMode);
			if (user != null) {
				identity.setUser(oldUser);
			}
		}
	}

	protected abstract Object work() throws Exception;

}

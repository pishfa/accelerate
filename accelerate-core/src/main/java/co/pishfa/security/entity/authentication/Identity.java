/**
 * 
 */
package co.pishfa.security.entity.authentication;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.security.exception.AuthenticationException;
import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.exception.LoginRequiredException;
import co.pishfa.security.entity.audit.AuditLevel;
import co.pishfa.security.entity.authorization.Permission;
import co.pishfa.security.service.*;
import co.pishfa.security.service.handler.AllPermissionHandler;
import co.pishfa.security.service.handler.PermissionScopeHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Taha Ghasemi
 * 
 */
@Named("identity")
@SessionScoped
public class Identity implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(Identity.class);

	@Inject
	private SecurityConfig securityConfig;

	@Inject
	private AuthenticationService authenticationService;

	@Inject
	private AuthorizationService authorizationService;

	@Inject
	private AuditService auditService;

	private boolean systemMode = false;

	private OnlineUser onlineUser;
	private User user;

	private final static AllPermissionHandler allPermissionHandler = new AllPermissionHandler();

	public static Identity getInstance() {
		return CdiUtils.getInstance(Identity.class);
	}

	@PostConstruct
	public void init() {
		log.info("init");
	}

	/**
	 * @return the onlineUser
	 */
	public OnlineUser getOnlineUser() {
		return onlineUser;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

    public User getUserAttached() {
        return authenticationService.getCurrentUserAttached();
    }

	/**
	 * @param onlineUser
	 *            the onlineUser to set
	 */
	public void setOnlineUser(final OnlineUser onlineUser) {
		this.onlineUser = onlineUser;
		this.user = onlineUser == null ? null : onlineUser.getUser();
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(final User user) {
		this.user = user;
		this.onlineUser.setUser(user);
	}

	public boolean is(final String role) {
		return hasRole(role);
	}

	public boolean canAny(final String actions) {
		return hasOneOfPermissions(null, StringUtils.split(actions, '|'));
	}

	public boolean canAny(final Object target, final String actions) {
		return hasOneOfPermissions(target, StringUtils.split(actions, '|'));
	}

	public boolean can(final String action) {
		return can(null, action);
	}

	public boolean can(final Object target, final String action) {
		return hasPermission(target, action);
	}

	public boolean isLoggedIn() {
		if (shouldBypassSecurity()) {
			return true;
		}
		return onlineUser.isLoggedIn();
	}

	public void setLoggedIn(final boolean loggedIn) {
		onlineUser.setLoggedIn(loggedIn);
	}

	public boolean shouldBypassSecurity() {
		return !securityConfig.isSecurityEnabled() || systemMode;
	}

	/**
	 * @return true if this identity has the given role.
	 */
	public boolean hasRole(final String role) {
		if (shouldBypassSecurity()) {
			return true;
		}
		return authorizationService.getImpliedRoles(user).containsKey(role);
	}

	/**
	 * Finds the permission for the given action.
	 */
	public Permission findPermission(final String action) {
		return authorizationService.findPermission(this, action);
	}

	public boolean hasOneOfPermissions(final Object target, final String... actions) {
		if (actions == null) {
			return hasPermission(target, null);
		}

		for (String action : actions) {
			if (hasPermission(target, action)) {
				return true;
			}
		}
		return false;
	}

	public void checkOneOfPermissions(final Object target, final String... actions) throws AuthorizationException {
		if (!hasOneOfPermissions(target, actions)) {
			if (isLoggedIn()) {
				String actionStrs = Arrays.toString(actions);
				auditService.audit(authorizationService.findAction(actionStrs), "unathorized.access", String.valueOf(target), AuditLevel.RISK);
				throw new AuthorizationException(target, actionStrs, getPermissionMsg(target, actionStrs));
			} else {
				throw new LoginRequiredException();
			}
		}
	}

	public void checkPermission(final Object target, final String action) throws AuthorizationException {
        if(action != null && action.startsWith("#")) {
            checkRole(action);
        }
		if (!hasPermission(target, action)) {
			if (isLoggedIn()) {
				auditService.audit(authorizationService.findAction(action), "unathorized.access", String.valueOf(target), AuditLevel.RISK);
				throw new AuthorizationException(target, action, getPermissionMsg(target, action));
			} else {
				throw new LoginRequiredException();
			}
		}
	}

	public String getPermissionMsg(final Object target, final String action) {
		String name = user.getName();
		return String.format("User %s is not authorized to perform %s on %s", name, action, target);
	}

	public void checkRole(final String role) throws AuthorizationException {
		if (!hasRole(role)) {
			String name = user.getName();
			if (isLoggedIn()) {
				throw new AuthorizationException(name, role, "User " + name + " has not the required role " + role);
			} else {
				throw new LoginRequiredException();
			}
		}
	}

	/**
	 * Checks whether the current identity has at least one of the roles
	 */
	public void checkOneOfRoles(final String... roles) throws AuthorizationException {
		if (!hasOneOfRoles(roles)) {
			String name = user.getName();
			if (isLoggedIn()) {
				throw new AuthorizationException(name, roles.toString(), "User " + name
						+ " has none of the required roles");
			} else {
				throw new LoginRequiredException();
			}
		}
	}

	public boolean hasOneOfRoles(final String... roles) {
		for (String role : roles) {
			if (hasRole(role)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPermission(final Object target, final String action) {
		if (shouldBypassSecurity()) {
			return true;
		}
        if(action != null && action.startsWith("#")) {
            return hasRole(action.substring(1));
        }
		return authorizationService.hasPermission(this, target, action);
	}

	public boolean isSystemMode() {
		return systemMode;
	}

	/**
	 * @param systemMode
	 *            the systemMode to set
	 */
	public void setSystemMode(final boolean systemMode) {
		this.systemMode = systemMode;
	}

	public void login(final String username, final String password) throws AuthenticationException {
		authenticationService.login(username, password);
	}

	public void logout() {
		authenticationService.logout(getOnlineUser());
	}

	public static Identity getFromSession(HttpSession session) {
		Validate.notNull(session);

		return (Identity) session.getAttribute(SecurityConstants.SESSION_IDENTITY);
	}

	public void putInSession(HttpSession session) {
		Validate.notNull(session);

		session.setAttribute(SecurityConstants.SESSION_IDENTITY, this);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <E> PermissionScopeHandler<E> getScopeHandler(Permission permission) {
		if (shouldBypassSecurity()) {
			return (PermissionScopeHandler) allPermissionHandler;
		}
		return authorizationService.getScopeHandler(permission);
	}

}

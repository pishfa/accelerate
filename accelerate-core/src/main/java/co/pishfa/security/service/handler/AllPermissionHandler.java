/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authorization.Permission;

/**
 * @author Taha Ghasemi
 * 
 */
@ScopeHandler("all")
public class AllPermissionHandler implements PermissionScopeHandler<Object> {

	@Override
	public boolean check(Identity identity, Object target, String action, Permission permission)
			throws AuthorizationException {
		return true;
	}

	@Override
	public void addConditions(Identity identity, Permission permission, QueryBuilder<Object> query) {
		// no condition
	}

}

/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authorization.Permission;
import co.pishfa.security.entity.authorization.PermissionDef;

/**
 * @author Taha Ghasemi
 * 
 */
@ScopeHandler("user_defined_perm_def")
public class UserDefinedPermDefHandler implements PermissionScopeHandler<PermissionDef> {

	@Override
	public boolean check(Identity identity, PermissionDef target, String action, Permission permission)
			throws AuthorizationException {
		return target.getRepresentative() != null; // should be user defined
	}

	@Override
	public void addConditions(Identity identity, Permission permission, QueryBuilder<PermissionDef> query) {
		query.and("representative != null");
	}

}

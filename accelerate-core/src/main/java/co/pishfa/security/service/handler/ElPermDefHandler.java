/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authorization.Permission;
import co.pishfa.security.entity.authorization.PermissionDefParam;

/**
 * @author Taha Ghasemi
 * 
 */
@ScopeHandler("el")
public class ElPermDefHandler implements PermissionScopeHandler<BasedOnOtherPermission> {

	@Override
	public boolean check(Identity identity, BasedOnOtherPermission target, String action, Permission permission)
			throws AuthorizationException {
		PermissionDefParam param = permission.getDefinition().getParam("el");
		if (param != null) {
			// TODO evaluate param as an el
			return false;
		} else {
			throw new IllegalArgumentException("The permision def should have a parameter named el");
		}
	}

	@Override
	public void addConditions(Identity identity, Permission permission, QueryBuilder<BasedOnOtherPermission> query) {
		// TODO parse el and generate corresponding sql script from it
	}

}

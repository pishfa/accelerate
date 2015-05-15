/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.accelerate.exception.NotSupportedException;
import co.pishfa.security.entity.authentication.User;
import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authorization.Permission;

/**
 * @author Taha Ghasemi
 * 
 */
@ScopeHandler("my")
public class MyPermissionHandler implements PermissionScopeHandler<Object> {

	@Override
	public boolean check(Identity identity, Object target, String action, Permission permission)
			throws AuthorizationException {
        if(target instanceof User)
            return identity.getUser().equals(target);
        else
		    return identity.equals(target);
	}

	@Override
	public void addConditions(Identity identity, Permission permission, QueryBuilder<Object> query) {
		throw new NotSupportedException();
	}

}

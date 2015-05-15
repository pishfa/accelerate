/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authorization.Permission;
import co.pishfa.security.entity.authorization.SecuredEntity;

/**
 * @author Taha Ghasemi
 * 
 */
@ScopeHandler("self")
public class SelfPermissionHandler implements PermissionScopeHandler<SecuredEntity> {

	@Override
	public boolean check(Identity identity, SecuredEntity target, String action, Permission permission)
			throws AuthorizationException {
		if (target.getCreatedBy() != null) {
			return target.getCreatedBy().equals(identity.getUser());
		} else {
			return false; // when object does created by anybody means it cannot
							// be accessed by the self rule
		}
	}

	@Override
	public void addConditions(Identity identity, Permission permission, QueryBuilder<SecuredEntity> query) {
		query.and("e.createdBy.id = :createdBy").with("createdBy", identity.getUser().getId());
	}

}

/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.entity.authorization.PermissionDefParam;
import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authorization.OwnableEntity;
import co.pishfa.security.entity.authorization.Permission;

/**
 * @author Taha Ghasemi
 * 
 */
@ScopeHandler("own")
public class OwnPermissonHandler implements PermissionScopeHandler<OwnableEntity> {

    private static final String PROPERTY_PARAM = "property";

	@Override
	public boolean check(Identity identity, OwnableEntity target, String action, Permission permission)
			throws AuthorizationException {
		OwnableEntity targetObj = target;
		if (targetObj.getOwner() != null) {
			return targetObj.getOwner().equals(identity.getUser());
		} else {
			return false;
		}
	}

	@Override
	public void addConditions(Identity identity, Permission permission, QueryBuilder<OwnableEntity> query) {
        PermissionDefParam property = permission.getDefinition().getParam(PROPERTY_PARAM);
        String field = property == null? "owner" : property.getValue();
        query.andEntityFieldEquals(field, identity.getUser());
	}

}

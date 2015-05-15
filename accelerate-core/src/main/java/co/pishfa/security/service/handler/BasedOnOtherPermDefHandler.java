/**
 * 
 */
package co.pishfa.security.service.handler;

import org.apache.commons.lang3.StringUtils;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authorization.Permission;
import co.pishfa.security.entity.authorization.PermissionDefParam;

/**
 * @author Taha Ghasemi
 * 
 */
@ScopeHandler("other")
public class BasedOnOtherPermDefHandler implements PermissionScopeHandler<BasedOnOtherPermission> {

	private static final String PERMISSION_PARAM = "permission";

	@Override
	public boolean check(Identity identity, BasedOnOtherPermission target, String action, Permission permission)
			throws AuthorizationException {
		PermissionDefParam param = permission.getDefinition().getParam(PERMISSION_PARAM);
		if (param != null) {
			return identity.hasOneOfPermissions(target == null ? null : target.getOtherEntity(),
					StringUtils.split(param.getValue(), '|'));
		} else {
			throw new IllegalArgumentException("The permision def should have a parameter named permission");
		}
	}

	@Override
	public void addConditions(Identity identity, Permission permission, QueryBuilder<BasedOnOtherPermission> query) {
		// TODO implement this
		throw new RuntimeException("Not implemented yet");
	}

}

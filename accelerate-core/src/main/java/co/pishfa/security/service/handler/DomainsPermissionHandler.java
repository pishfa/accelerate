/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.entity.authorization.*;
import co.pishfa.security.repo.DomainRepo;
import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authentication.Domain;
import co.pishfa.security.entity.authentication.User;
import co.pishfa.security.entity.authorization.BaseSecuredEntity;

/**
 * @author Taha Ghasemi
 * 
 */
@ScopeHandler("domains")
public class DomainsPermissionHandler implements PermissionScopeHandler<BaseSecuredEntity> {

	@Override
	public boolean check(Identity identity, BaseSecuredEntity target, String action, Permission permission)
			throws AuthorizationException {
		return checkDomain(identity, target) && checkSecurityLevel(identity, target)
				&& checkAccessLevel(permission.getDefinition(), target);
	}

	protected boolean checkAccessLevel(PermissionDef def, BaseSecuredEntity targetObj) {
		if (targetObj.getAccessLevel() != null) {
			AccessLevel requiredLevel = computeRequiredLevel(def);
			return requiredLevel.getLevel() <= targetObj.getAccessLevel().getLevel();
		}
		return true;
	}

	private AccessLevel computeRequiredLevel(PermissionDef def) {
		PermissionDefParam param = def.getParam("requiredLevel");
		AccessLevel requiredLevel = AccessLevel.READ_WRITE; // by default
		if (param != null) {
			requiredLevel = AccessLevel.valueOf(param.getValue());
		}
		return requiredLevel;
	}

	protected boolean checkSecurityLevel(Identity identity, BaseSecuredEntity targetObj) {
		if (targetObj.getSecurityLevel() != null) {
			if (identity.getUser().getCurrentLevel() == null) {
				return false;
			}
			return identity.getUser().getCurrentLevel().getLevel() >= targetObj.getSecurityLevel().getLevel();
		}
		return true;
	}

	protected boolean checkDomain(Identity identity, BaseSecuredEntity targetObj) {
		if (targetObj.getDomain() != null) {
			return targetObj.getDomain().containedIn(identity.getUser().getDomain());
		} else {
			return true; // when object does not belong to any domain it means it can be accessed by every one
		}
	}

	@Override
	public void addConditions(Identity identity, Permission permission, QueryBuilder<BaseSecuredEntity> query) {
		query.append(" and e.securityLevel <= :user_sec_level ");
		query.append(" and e.accessLevel >= :perm_req_level ");
		User user = identity.getUser();
		addDomainConditions(query, user.getDomain());
		query.with("user_sec_level", user.getCurrentLevel());
		query.with("perm_req_level", computeRequiredLevel(permission.getDefinition()));
	}

	protected void addDomainConditions(QueryBuilder<BaseSecuredEntity> query, Domain userDomain) {
		// TODO is null is not work here since Domain inherit from Principal and hibernate use inner join and thus no
		// null
		if (userDomain == null) {
			query.append(" and e.domain.id = :shared_id ");
		} else {
			query.append(
					" and (e.domain.id = :shared_id or e.domain.code between :domain_start and :domain_end) ")
					.with("domain_start", userDomain.getScopeStart())
					.with("domain_end", userDomain.getScopeEnd());
		}
		query.with("shared_id", DomainRepo.getInstance().getSharedDomainId());
	}

}

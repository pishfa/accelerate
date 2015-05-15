/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.entity.authentication.Domain;
import co.pishfa.security.repo.DomainRepo;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authorization.BaseSecuredEntity;

/**
 * @author Taha Ghasemi
 * 
 */
@ScopeHandler("domain")
public class DomainPermissionHandler extends DomainsPermissionHandler {

	@Override
	protected boolean checkDomain(Identity identity, BaseSecuredEntity targetObj) {
		if (targetObj.getDomain() != null) {
			return targetObj.getDomain().equals(identity.getUser().getDomain());
		} else {
			return true; // when object does not belong to any domain it means it can be accessed by every one
		}
	}

	@Override
	protected void addDomainConditions(QueryBuilder<BaseSecuredEntity> query, Domain userDomain) {
		if (userDomain == null) { // a user with null domain can only see entities with null domains
			query.append(" and e.domain.id = :shared_id ");
		} else {
			query.append(" and (e.domain.id = :shared_id or e.domain.id = :sec_domain_id) ").with(
					"sec_domain_id", userDomain.getId());
		}
		query.with("shared_id", DomainRepo.getInstance().getSharedDomainId());
	}

}

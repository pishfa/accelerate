/**
 * 
 */
package co.pishfa.security.entity.authentication;

import co.pishfa.accelerate.persistence.repository.EntityRepository;
import co.pishfa.security.repo.DomainRepo;
import co.pishfa.security.repo.RepresentativeRepo;
import co.pishfa.security.repo.UserRepo;

/**
 * @author Taha Ghasemi
 * 
 */
public enum PrincipalType {
	USER("manage.user"),
	DOMAIN("manage.domain"),
    CUSTOM_PERM_DEF("manage.permissionDef"),
    ROLE("manage.role"),
    ;

	private PrincipalType(String action) {
		this.action = action;
	}

	private final String action;

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @return
	 */
	public EntityRepository<? extends Principal,Long> getRepository() {
		switch (this) {
		case USER:
			return UserRepo.getInstance();
		case DOMAIN:
			return DomainRepo.getInstance();
		case CUSTOM_PERM_DEF:
			return RepresentativeRepo.getInstance();
		}
		return null;
	}

}

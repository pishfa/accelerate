/**
 * 
 */
package co.pishfa.security.entity.authorization;

import co.pishfa.accelerate.clone.CloneIgnore;
import co.pishfa.accelerate.clone.CloneNull;
import co.pishfa.accelerate.clone.CloneShallow;
import co.pishfa.accelerate.entity.common.BaseEntity;
import co.pishfa.security.entity.authentication.Domain;
import co.pishfa.security.entity.authentication.User;

import javax.persistence.*;

/**
 * Base implementation of {@link SecuredEntity}
 * 
 * @author Taha Ghasemi
 * 
 */
@MappedSuperclass
public abstract class BaseSecuredEntity extends BaseEntity implements SecuredEntity<Long> {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, cascade = {}, optional = true)
	@CloneShallow
	private User createdBy;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH }, optional = true)
	// @OnDelete(action = OnDeleteAction.NO_ACTION)
	// since when we want to add the entity, its domain will be set based on the createdBy
	@CloneShallow
	// @Fetch(FetchMode.SELECT)
	protected Domain domain;

	@Enumerated(EnumType.ORDINAL)
	protected SecurityLevel securityLevel = SecurityLevel.UNCLASSIFIED;

	@Enumerated(EnumType.ORDINAL)
	@CloneIgnore
	protected AccessLevel accessLevel = AccessLevel.READ_WRITE_DELETE;

	@Override
	public Domain getDomain() {
		return domain;
	}

	@Override
	public void setDomain(Domain secDomain) {
		this.domain = secDomain;
	}

	@Override
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel level) {
		this.securityLevel = level;
	}

	@Override
	public AccessLevel getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(AccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}

	public void setAccessLevelByName(String name) {
		setAccessLevel(AccessLevel.valueOf(name));
	}

	/*
	 * @PreRemove public void onPreRemove() { if (accessLevel.getLevel() < AccessLevel.READ_WRITE_DELETE.getLevel()) {
	 * throw new RuntimeException("Can not delete entity since its access level is " + accessLevel); } }
	 * 
	 * @PreUpdate public void onPreUpdate() { if (accessLevel.getLevel() < AccessLevel.READ_WRITE.getLevel()) { throw
	 * new RuntimeException("Can not update entity since its access level is " + accessLevel); } }
	 */

	@Override
	public User getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

}

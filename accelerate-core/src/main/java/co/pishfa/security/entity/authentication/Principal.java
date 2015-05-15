/**
 * 
 */
package co.pishfa.security.entity.authentication;

import co.pishfa.security.entity.authorization.BaseSecuredEntity;
import co.pishfa.security.repo.PermissionRepo;
import co.pishfa.security.repo.RoleAssignmentRepo;

import javax.persistence.Cacheable;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostRemove;

/**
 * @author Taha Ghasemi
 * 
 */
@MappedSuperclass
@Cacheable
public abstract class Principal extends BaseSecuredEntity {

	/*
	 * @Id
	 * 
	 * @GeneratedValue(generator = "seq_or_table")
	 * 
	 * @GenericGenerator(name = "seq_or_table", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator") protected
	 * Long id;
	 */

	private static final long serialVersionUID = 1L;

	@Embedded
	protected Activation activation = new Activation();

	public Activation getActivation() {
		return activation;
	}

	public void setActivation(Activation activation) {
		this.activation = activation;
	}

	/**
	 * 
	 * @return the precedence which will be set into the access rule associated to the principal
	 */
	public int getPrecedenceBase() {
		return 0;
	}

	public boolean isActive() {
		return activation.isActive();
	}

	public abstract PrincipalType getType();

	@PostRemove
	public void postRemove() {
		// Delete related access rules
		PermissionRepo.getInstance().deleteByPrincipal(this);
        RoleAssignmentRepo.getInstance().deleteByPrincipal(this);
	}

}

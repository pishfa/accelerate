/**
 * 
 */
package co.pishfa.security.entity.authorization;

import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.security.entity.authentication.Principal;
import co.pishfa.security.repo.RoleRepo;

import javax.persistence.*;

/**
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_role_assignment")
@InitEntity(properties = @InitProperty(name = "name", value = "#{this.definition.name}:#{this.principal.name}"))
public class RoleAssignment extends AccessRule {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, optional = false)
	private Role role;

	public RoleAssignment() {
	}

	public RoleAssignment(Principal principal, Role role) {
		setPrincipal(principal);
		setRole(role);
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setRoleByName(String name) {
		try {
			setRole(RoleRepo.getInstance().findByName(name));
		} catch (Exception e) {
			throw new RuntimeException("Could not find a role with name " + name, e);
		}
	}

}

package co.pishfa.security.entity.authorization;

import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.security.entity.authentication.Principal;

import javax.persistence.*;

/**
 * such as allow view.city:secDomain() to usergroup(1)
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_permission")
@InitEntity(properties = @InitProperty(name = "name", value = "#{this.definition.name}:#{this.principal.name}"))
public class Permission extends AccessRule {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	private PermissionDef definition;

	public Permission() {
	}

	public Permission(PermissionDef definition, Principal principal) {
		this.definition = definition;
		this.principal = principal;
	}

	public PermissionDef getDefinition() {
		return definition;
	}

	/**
	 * @param definition
	 *            the definition to set
	 */
	public void setDefinition(PermissionDef definition) {
		this.definition = definition;
	}

}
package co.pishfa.security.entity.authorization;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import co.pishfa.accelerate.initializer.model.InitProperty;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Cacheable
@Table(name = "ac_role")
public class Role extends AccessRuleDef {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, optional = false)
	@InitProperty("@parent(1)")
	private RoleCategory category;

	private String title;

	private String description;

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Role> roles;

	public RoleCategory getCategory() {
		return category;
	}

	public void setCategory(RoleCategory category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}

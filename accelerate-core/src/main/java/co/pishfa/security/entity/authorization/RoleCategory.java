/**
 * 
 */
package co.pishfa.security.entity.authorization;

import co.pishfa.accelerate.entity.hierarchical.BaseHierarchicalEntity;

import javax.persistence.*;
import java.util.List;

/**
 * Grouping of roles for ease of presentation to the users
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_role_category")
public class RoleCategory extends BaseHierarchicalEntity<RoleCategory> {

	private static final long serialVersionUID = 1L;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "category")
	private List<Role> roles;

	private String title;

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}

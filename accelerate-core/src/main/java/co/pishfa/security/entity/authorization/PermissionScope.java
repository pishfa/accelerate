/**
 * 
 */
package co.pishfa.security.entity.authorization;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import co.pishfa.accelerate.entity.common.BaseEntity;

/**
 * such as all, self, secDomain, custom, ...
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_permission_scope", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Cacheable
public class PermissionScope extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String title;

	private String description;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "scope")
	private List<PermissionDef> permissionDefs;

	public PermissionScope() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<PermissionDef> getPermissionDefs() {
		return permissionDefs;
	}

	public void setPermissionDefs(List<PermissionDef> permissionDefs) {
		this.permissionDefs = permissionDefs;
	}

}

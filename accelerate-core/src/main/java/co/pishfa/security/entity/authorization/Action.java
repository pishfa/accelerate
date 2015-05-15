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

import co.pishfa.accelerate.entity.hierarchical.BaseParentsHierarchicalEntity;
import co.pishfa.security.entity.audit.Auditable;

/**
 * such as city.add, city.view,...
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name="ac_action", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Cacheable
public class Action extends BaseParentsHierarchicalEntity<Action> implements Auditable {

	private static final long serialVersionUID = 1L;

	private String title;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "action")
	private List<PermissionDef> permissionDefs;

    private String image;

    private String icon;

    private String help;

	public Action() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }
}

/**
 * 
 */
package co.pishfa.accelerate.entity.hierarchical;

import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import java.util.ArrayList;
import java.util.List;

/**
 * This entity is a hierarchical entity which also keep the list of its parents. This way, finding all children of an
 * entity is fast.
 * 
 * @author Taha Ghasemi
 * 
 */
@MappedSuperclass
@InitEntity(properties = @InitProperty(alias = "parent", name = "parentAndProperties", value = "@parent?"))
public abstract class BaseParentsHierarchicalEntity<T extends BaseParentsHierarchicalEntity<T>> extends
        BaseHierarchicalEntity<T> {

	private static final long serialVersionUID = 1L;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<T> parents;

	public List<T> getParents() {
		return parents;
	}

	public void setParents(List<T> parents) {
		this.parents = parents;
	}

	public boolean containedIn(T parent) {
		return getParents().contains(parent);
	}

    /**
     * This method is developed for add operation in initializer.
     */
    @Override
    public void setParentAndProperties(T parent) {
        super.setParentAndProperties(parent);
        if (parent != null) {
            List<T> parents = new ArrayList<T>();
            if (parent.getParents() != null) {
                parents.addAll(parent.getParents());
            }
            parents.add(parent);
            this.setParents(parents);
        } else {
            this.setParents(null);
        }
    }

}

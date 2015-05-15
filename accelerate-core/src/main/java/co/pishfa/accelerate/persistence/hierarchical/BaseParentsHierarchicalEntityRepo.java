/**
 * 
 */
package co.pishfa.accelerate.persistence.hierarchical;

import co.pishfa.accelerate.entity.hierarchical.BaseParentsHierarchicalEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Taha Ghasemi
 * 
 */
public class BaseParentsHierarchicalEntityRepo<T extends BaseParentsHierarchicalEntity<T>> extends
        BaseHierarchicalEntityJpaRepo<T, Long> {

	public BaseParentsHierarchicalEntityRepo() {
		super();
	}

	/**
	 * Find the descendants of this entity using the parents property.
	 */
	@Override
	public List<T> findDescendants(T entity) {
		return query().select().where(":this in (e.parents)").with("this", entity).list();
	}

    @Override
    public void setParent(T parent, T child, boolean cascadeToChildren) {
        super.setParent(parent, child, cascadeToChildren);
        if (parent != null) {
            List<T> parents = new ArrayList<T>();
            if (parent.getParents() != null) {
                parents.addAll(parent.getParents());
            }
            parents.add(parent);
            child.setParents(parents);
        } else {
            child.setParents(null);
        }
    }
}

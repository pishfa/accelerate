/**
 * 
 */
package co.pishfa.accelerate.persistence.hierarchical;

import co.pishfa.accelerate.entity.hierarchical.HierarchicalEntity;
import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

/**
 * By default it is assumed that the name of parent property is "parent". You can override {@link #parentPropertyName()}
 * to change this.
 * 
 * @author Taha Ghasemi
 */
public abstract class BaseHierarchicalEntityJpaRepo<T extends HierarchicalEntity<T,K>, K> extends
        BaseJpaRepo<T, K> implements HierarchicalEntityService<T,K> {

	public static final String PARENT_PROPERTY = "parent";

	public BaseHierarchicalEntityJpaRepo(Class<T> entityClass, Class<K> keyClass) {
		super(entityClass, keyClass);
	}

	public BaseHierarchicalEntityJpaRepo() {
	}

	protected String parentPropertyName() {
		return PARENT_PROPERTY;
	}

	@Override
	public List<T> findDirectChildren(T entity, Filter filter) {
		Validate.notNull(entity);

		return query().select().where(filter).andEntityField(parentPropertyName()).append(".id=:parent_id")
				.with("parent_id", entity.getId()).sort().paged().list();
	}

	@Override
	public boolean hasChild(T entity, Filter filter) {
		Validate.notNull(entity);

		return (Long) (query().selectCount().where(filter).andEntityField(parentPropertyName()).append(".id=:parent_id")
				.with("parent_id", entity.getId()).result(Long.class)) > 0;
	}

	@Override
	public List<T> findOrderByDepth(Integer levels, Filter filter) {
        QueryBuilder<T> q = query().select().whereTrue().and(filter);
        if(levels > 0) q.andEntityField("depth").append(" < :depth").with("depth", levels);
        q.sort("e.depth", false);
        return q.list();
	}

	/**
	 * Delete this entity and all of its descendants. To find the descendants this method uses
	 * {@link #findDescendants(HierarchicalEntity)}. To delete them, this method calls {@link #deleteAll(List)} and not
	 * the deleteBuck operation.
	 * 
	 */
	public void deleteWithDescendants(T entity) {
		List<T> descendants = findDescendants(entity);
		deleteAll(descendants);
	}

	/**
	 * Finds all the descendants of the given entity (including the entity itself). Descendants with higher depth are
	 * added first. This method uses a naive approach of DFS by calling
	 * {@link #addDescendants(HierarchicalEntity, List)}
	 * 
	 */
	public List<T> findDescendants(T entity) {
		List<T> descendants = new ArrayList<T>();
		addDescendants(entity, descendants);
		return descendants;
	}

	/**
	 * Finds all the descendants of the given entity and adds them to the provided list (including the entity itself).
	 * Descendants with higher depth should be added first. This method uses a naive approach of DFS.
	 * 
	 */
	protected void addDescendants(T entity, List<T> descendants) {
		List<T> child = findDirectChildren(entity, null);
		for (T node : child) {
			addDescendants(node, descendants);
		}
		descendants.add(entity);
	}

	@Override
	public T findRoot(Filter filter) {
		return (T) query().select().where(filter).andEntityField(parentPropertyName()).append(" is null").result();
	}

    @Override
    public List<T> findRoots(Filter filter) {
        return query().select().where(filter).andEntityField(parentPropertyName()).append(" is null").list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addChild(T parent, T child) {
        parent.getChildren().add(child);
        setParent(parent, child, true);
    }

    @Override
    public void removeChild(T parent, T child) {
        parent.getChildren().remove(child);
        if (parent.getChildren().isEmpty()) {
            parent.setLeaf(true);
        }
        child.setParent(null);
    }

    @Override
    public void setParent(T parent, T child, boolean cascadeToChildren) {
        child.setParent(parent);
        if (parent != null) {
            parent.setLeaf(false);
            child.setDepth(parent.getDepth() + 1);
        } else {
            child.setDepth(0);
        }
		if(cascadeToChildren) {
			List<T> children = child.getChildren();
			if (children != null) {
				for (T c : children) {
					setParent(child, c, true);
				}
			}
		}
    }



}

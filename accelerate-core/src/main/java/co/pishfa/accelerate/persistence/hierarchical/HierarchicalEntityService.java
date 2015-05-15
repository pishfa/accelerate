/**
 * 
 */
package co.pishfa.accelerate.persistence.hierarchical;

import co.pishfa.accelerate.entity.hierarchical.HierarchicalEntity;
import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.service.EntityService;

import java.util.List;

/**
 * @author Taha Ghasemi
 * 
 */
public interface HierarchicalEntityService<T extends HierarchicalEntity<T,K>,K> extends EntityService<T,K> {

	/**
	 * Finds those direct children of the given entity that satisfy the filter.
	 * If filter specifies ordering and paginations the children are also ordered and/or paginated.
	 * 
	 * @param entity
	 * @param filter
	 *            could be null which means no filtering
	 * @return
	 */
	List<T> findDirectChildren(T entity, Filter filter);

	/**
	 * Check whether the given entity has any direct children which satisfies the filter conditions.
	 * Note that it should not rely on isLeaf property since it may be out of date.
	 * 
	 * @param entity
	 * @param filter
	 *            could be null which means no filtering
	 * @return
	 */
	boolean hasChild(T entity, Filter filter);

	/**
	 * Retrieves all the nodes that satisfies the filter from root of the tree up to the given levels down. The nodes are sorted in
	 * increasing order of their depth.
	 * 
	 * @param levels
	 *            how many levels to retrieve. Null means all.
	 * @param filter
	 * @return
	 */
	List<T> findOrderByDepth(Integer levels, Filter filter);

	/**
	 * Finds the first node that satisfies the filter and with no parent.
	 * 
	 * @param filter
	 * @return null if not found.
	 */
	T findRoot(Filter filter);

    /**
     * Finds all the nodes that satisfies the filter and with no parent.
     *
     * @param filter
     * @return null if not found.
     */
    List<T> findRoots(Filter filter);

    void addChild(T parent, T child);

    void removeChild(T parent, T child);

    /**
     * Set the parent and also set the other required properties. Cascade these to children if requested.
     *
     * @param parent
     * @param cascadeToChildren
     */
    void setParent(T parent, T child, boolean cascadeToChildren);

}

/**
 * 
 */
package co.pishfa.accelerate.entity.hierarchical;

import co.pishfa.accelerate.entity.common.Entity;

import java.util.List;

/**
 * Representation of tree-structured entities
 * 
 * @author Taha Ghasemi
 */
public interface HierarchicalEntity<T extends HierarchicalEntity<T,K>,K> extends Entity<K> {

	T getParent();

	List<T> getChildren();

	/**
	 * Set the parent without changing other related or required properties.
	 * 
	 * @param parent
	 */
	void setParent(T parent);

	int getDepth();

	/**
	 * Sets the depth of this node. Note that this is not propagated to the children. You should manually do this if required.
	 * 
	 * @param depth
	 */
	void setDepth(int depth);

	boolean isLeaf();

	void setLeaf(boolean leaf);

}

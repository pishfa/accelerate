/**
 * 
 */
package co.pishfa.accelerate.entity.hierarchical;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.security.entity.authorization.BaseSecuredEntity;

/**
 * @author Taha Ghasemi
 * 
 */
@MappedSuperclass
@InitEntity(properties = @InitProperty(alias = "parent", name = "parentAndProperties", value = "@parent?"))
public abstract class BaseHierarchicalEntity<T extends HierarchicalEntity<T,Long>> extends BaseSecuredEntity
		implements HierarchicalEntity<T,Long> {

	private static final long serialVersionUID = 1L;

	@Min(0)
	protected int depth = 0;

	protected boolean leaf = true;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, optional = true)
	protected T parent;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parent"/*, orphanRemoval = true*/)
	protected List<T> children = new ArrayList<>();

	@Override
	public T getParent() {
		return parent;
	}

	@Override
	public void setParent(T parent) {
		this.parent = parent;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public boolean isLeaf() {
		return leaf;
	}

	@Override
	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	@Override
	public List<T> getChildren() {
		return children;
	}

	public void setChildren(List<T> children) {
		this.children = children;
	}

    /**
     * This method is developed for add operation in initializer.
     */
    public void setParentAndProperties(T parent) {
        this.setParent(parent);
        if (parent != null) {
            parent.setLeaf(false);
            this.setDepth(parent.getDepth() + 1);
            parent.getChildren().add((T) this);
        } else {
            this.setDepth(0);
        }
    }

}

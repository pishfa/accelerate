package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.entity.common.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * This is like {@link EntityChildMgmt} but it does not rely on parent to do the persist operations. Note that
 * these operations are happen during commit times (when parent wants to be saved).
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 * @param <T>
 *            type of entities of this controller
 * @param <P>
 *            type of parent entity
 * 
 */
public abstract class IndependentEntityChildMgmt<T extends Entity<Long>, P extends Entity<Long>> extends
        EntityChildMgmt<T, P> {

	private static final long serialVersionUID = 1L;

	private List<T> deleted;

	public IndependentEntityChildMgmt(Class<T> entityClass) {
		super(entityClass);
	}

	@Override
	protected List<T> findData() {
		deleted = new ArrayList<>();
		return super.findData();
	}

	@Override
	protected void deleteCurrent() {
		deleted.add(getCurrent());
		super.deleteCurrent();
	}

	@Override
	public void commit(P attachedParrent) {
		super.commit(attachedParrent);
		for (T entity : getData()) {
			setEntityParent(entity, attachedParrent);
			saveEntity(entity);
		}
		for (T entity : deleted) {
			if (entity.getId() != null) { // if it is not newly added entity
				deleteEntity(entity);
			}
		}
	}

}

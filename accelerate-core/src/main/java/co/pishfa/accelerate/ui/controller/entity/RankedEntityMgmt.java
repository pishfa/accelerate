package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.service.RankedEntityService;
import co.pishfa.accelerate.entity.common.RankedEntity;
import co.pishfa.accelerate.ui.UiAction;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class RankedEntityMgmt<T extends RankedEntity<K>, K> extends EntityMgmt<T, K> {

	private static final long serialVersionUID = 1L;

	private Integer maxRank;

	public void setMaxRank(Integer maxRank) {
		this.maxRank = maxRank;
	}

	public Integer getMaxRank() {
		if(maxRank == null) {
			maxRank = findMaxRank();
		}
		return maxRank;
	}

	protected int findMaxRank() {
		return getEntityService().maxRank(getRankFilter());
	}

	@Override
	public RankedEntityService<T,K> getEntityService() {
		return (RankedEntityService<T,K>) super.getEntityService();
	}

	protected Filter<T> getRankFilter() {
		return null;
	}

	public boolean canUp(T entity) {
		return canEdit(entity) && entity.getRank() > 1;
	}

	@UiAction
	public String up() {
		T current = getEntityService().setRank(getRankFilter(), getCurrent(), getCurrent().getRank() - 1);
		load();
		setCurrent(current);
		return null;
	}

	public boolean canDown(T entity) {
		return canEdit(entity) && entity.getRank() < getMaxRank();
	}

	@UiAction
	public String down() {
		T current = getEntityService().setRank(getRankFilter(), getCurrent(), getCurrent().getRank() + 1);
		load();
		setCurrent(current);
		return null;
	}

	@Override
	protected T newEntity() {
		T e = super.newEntity();
		e.setRank(getMaxRank() + 1);
		return e;
	}

	@Override
	protected T saveEntity(T entity) {
		if (!getEditMode())
			maxRank = getMaxRank() + 1;
		return super.saveEntity(entity);
	}

}

package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.service.RankedEntityService;
import co.pishfa.accelerate.entity.common.RankedEntity;
import co.pishfa.accelerate.ui.UiAction;
import co.pishfa.accelerate.ui.UiMessage;

/**
 * Keeps continues ranks
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class RankedEntityMgmt<T extends RankedEntity<K>, K> extends EntityMgmt<T, K> {

	private static final long serialVersionUID = 1L;

	private Integer maxRank;
	private Integer prevRank; //keep track of rank change

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
		return canEdit(entity) && entity.getRank() > 1; //1 must be replaced by minRank
	}

	@UiAction
	public String up() {
		if(getData().get(0) == getCurrent()) {
			setCurrentPage(getCurrentPage()-1);
		}
		setPrevCurrent(upEntity(getCurrent()));
		load();
		return null;
	}

	protected T upEntity(T entity) {
		return getEntityService().setRank(getRankFilter(), entity, entity.getRank() - 1);
	}

	public boolean canDown(T entity) {
		return canEdit(entity) && entity.getRank() < getMaxRank();
	}

	@UiAction
	public String down() {
		if(hasPagination() && getData().get(getPageSize()-1) == getCurrent()) {
			setCurrentPage(getCurrentPage()+1);
		}
		setPrevCurrent(downEntity(getCurrent()));
		load();
		return null;
	}

	protected T downEntity(T entity) {
		return getEntityService().setRank(getRankFilter(), entity, entity.getRank() + 1);
	}

	@Override
	protected void deleteEntity(T entity) {
		super.deleteEntity(entity);
		getEntityService().decrement(getRankFilter(), entity.getRank());
		maxRank = getMaxRank() - 1;
	}

	@Override
	protected T newEntity() {
		T e = super.newEntity();
		e.setRank(getMaxRank() + 1);
		return e;
	}

	@Override
	protected void addEdit() {
		super.addEdit();
		prevRank = getCurrent().getRank();
	}

	@Override
	@UiAction
	@UiMessage
	public String save() throws Exception {
		String res = super.save();
		//due to buck inc,dec we must clear pc
		getEntityService().clear();
		if(getCurrent() != null)
			setCurrentPage(1 + (getCurrent().getRank()-1) / getPageSize());
		return res;
	}

	@Override
	protected T saveEntity(T entity) {
		if (!getEditMode()) {
			maxRank = getMaxRank() + 1;
			if(entity.getRank() != prevRank) {
				getEntityService().increment(getRankFilter(), entity.getRank());
			}
		} else {
			//actually the entity rank is get updated by jpa before running dec,inc quries below
			int newRank = entity.getRank();
			entity.setRank(prevRank);
			if(newRank > prevRank) {
				entity.setRank(prevRank);
				getEntityService().decrement(getRankFilter(), prevRank + 1, newRank + 1);
			} else if(newRank < prevRank) {
				getEntityService().increment(getRankFilter(), newRank, prevRank);
			}
			entity.setRank(newRank);
		}
		return super.saveEntity(entity);
	}

}

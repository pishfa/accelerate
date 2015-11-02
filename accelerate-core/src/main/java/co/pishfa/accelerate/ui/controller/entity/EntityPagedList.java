package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.accelerate.ui.UiAction;
import co.pishfa.accelerate.ui.controller.Paginator;

import java.util.List;

/**
 * A controller that keeps a list of entities with filtering, pagination, and sorting capabilities.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 * @param <T>
 *            type of entities of this controller
 * 
 */
public class EntityPagedList<T extends Entity<K>, K> extends EntityFilterableList<T, K> implements Paginator {

	private static final long serialVersionUID = 1L;

	private Integer count;
	private int page = 1;

	public EntityPagedList(Class<T> entityClass, Class<K> keyClass) {
		super(entityClass, keyClass);
	}

	public EntityPagedList() {
		super();
	}

	@Override
	protected void setDefaultOptions() {
		super.setDefaultOptions();
		setOption(EntityControllerOption.PAGE_SIZE, 10);
        setSortAscending(true);
	}

	@Override
	public String load() {
		super.load();
        setCount(null);
		return null;
	}

	@Override
	public int getCount() {
		if (count == null) {
			setCount(findCount());
		}
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<T> findData(boolean withPagination) {
		if(!withPagination) {
			Object pageSize = removeOption(EntityControllerOption.PAGE_SIZE);
			List<T> res = findData();
			setOption(EntityControllerOption.PAGE_SIZE, pageSize);
			return res;
		} else {
			return findData();
		}
	}

	public int findCount() {
		if (hasOption(EntityControllerOption.PAGE_SIZE)) {
			return getEntityService().count(getFilter());
		} else {
			return getData().size();
		}
	}

    @Override
    public String reset() {
        this.page = 1;
        return super.reset();
    }

    /**
	 * @return null
	 */
	@UiAction
	public String page() {
		return load();
	}

	@Override
	public boolean hasPagination() {
		return hasOption(EntityControllerOption.PAGE_SIZE) && getCount() > getPageSize();
	}

	@Override
	public int getPageSize() {
		return (int) getOption(EntityControllerOption.PAGE_SIZE);
	}

	public void setPageSize(int pageSize) {
		setOption(EntityControllerOption.PAGE_SIZE, pageSize);
	}

	@Override
	public int getPageStart() {
		return (getCurrentPage() - 1) * getPageSize();
	}

	@Override
	public void addPagination(QueryBuilder<T> query) {
		if (hasPagination()) {
			query.max(getPageSize());
			query.first(getPageStart());
		}
	}

	@Override
	public int getCurrentPage() {
		return page;
	}

	/**
	 * @return null
	 */
	@Override
	@UiAction
	public String gotoPage(int page) {
		setCurrentPage(page);
		return load();
	}

	public void setCurrentPage(int page) {
		if (page >= 1 && page <= getNumOfPages()) {
			this.page = page;
		}
	}

	@Override
	public int getNumOfPages() {
		return (int) Math.ceil(getCount() / (float) getPageSize());
	}

	/**
	 * @return null
	 */
	@Override
	@UiAction
	public String nextPage() {
		return gotoPage(getCurrentPage() + 1);
	}

	@Override
	public boolean hasNextPage() {
		return getCurrentPage() < getNumOfPages();
	}

	/**
	 * @return null
	 */
	@Override
	@UiAction
	public String prevPage() {
		return gotoPage(getCurrentPage() - 1);
	}

	@Override
	public boolean hasPrevPage() {
		return getCurrentPage() > 1;
	}

	/**
	 * @return null
	 */
	@Override
	@UiAction
	public String lastPage() {
		return gotoPage(getNumOfPages());
	}

	/**
	 * @return null
	 */
	@Override
	@UiAction
	public String firstPage() {
		return gotoPage(1);
	}

}

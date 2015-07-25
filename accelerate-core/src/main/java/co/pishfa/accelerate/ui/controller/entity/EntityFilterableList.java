package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.persistence.filter.FilterField;
import co.pishfa.accelerate.persistence.filter.TypedFilterMetadata;
import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.accelerate.ui.UiAction;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.entity.authorization.Permission;
import co.pishfa.security.service.handler.PermissionScopeHandler;
import org.apache.deltaspike.core.util.ProxyUtils;

import java.util.Map;

/**
 * A controller that keeps a list of entities with filtering and ordering capabilities. It can filter data based on the
 * specified viewAction and the parent.
 * <p>
 * Using {@link FilterField} annotation you can specify the filtering condition on the parent field. If you annotate a field in this class with
 * {@link FilterField} it will be part of filtering process.
 * <p>
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 * @param <T>
 *            type of entities of this controller
 * 
 */
public class EntityFilterableList<T extends Entity<K>, K> extends EntityList<T, K> implements Filter<T> {

	private static final long serialVersionUID = 1L;

	private TypedFilterMetadata<T> filterMetadata;

	private Permission viewPermission;
	private PermissionScopeHandler<T> scopeHandler;

	public EntityFilterableList(Class<T> entityClass, Class<K> keyClass) {
		super(entityClass, keyClass);
	}

	public EntityFilterableList() {
		super();
	}

	@Override
	protected void init() {
		super.init();
		filterMetadata = gettTypedFilterMetadata();
		String viewAction = getViewAction();
		if (!StrUtils.isEmpty(viewAction) && hasOption(EntityControllerOption.SECURED)) {
			viewPermission = getIdentity().findPermission(viewAction);
			scopeHandler = getIdentity().getScopeHandler(viewPermission);
		}
	}

    /**
     * Creates a new typed filter metadata by investigating the current instance.
     * For performance reasons, you can override this method so it returns the per-class precomputed metadata.
     */
    protected TypedFilterMetadata<T> gettTypedFilterMetadata() {
        return new TypedFilterMetadata<T>(ProxyUtils.getUnproxiedClass(getClass()));
    }

    @Override
	public Filter<T> getFilter() {
		return this;
	}

	/**
	 * @return null
	 */
	@UiAction
	public String sort(String sortOn, boolean ascending) {
		setSortOn(sortOn);
		setSortAscending(ascending);
		load();
		return null;
	}

	@Override
	public void addConditions(QueryBuilder<T> query) {
		if (scopeHandler != null) {
			scopeHandler.addConditions(getIdentity(), viewPermission, query);
		}
		try {
			filterMetadata.addConditions(query, this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			getLogger().error("", e);
		}
	}

	@Override
	public void addSorting(QueryBuilder<T> query) {
		if (!StrUtils.isEmpty(getSortOn())) {
			// Important: injection is possible here so sortOn should be safe
			// only . , and _ is allowed
			String sortOn = getSortOn().replaceAll("[^\\._,0-9a-zA-Z]", "");
			boolean first = true;
			for (String sortField : sortOn.split(",")) {
				if (!first) {
					query.append(", ");
				} else {
					first = false;
				}
				query.append(sortField).sortDir(getSortAscending() == null ? true : getSortAscending());
			}
		}
	}

	public String getSortOn() {
		return (String) getOption(EntityControllerOption.SORT_ON);
	}

	public void setSortOn(String sortOn) {
		setOption(EntityControllerOption.SORT_ON, sortOn);
	}

	public Boolean getSortAscending() {
        return (Boolean) getOption(EntityControllerOption.SORT_ASCENDING);
	}

	public void setSortAscending(Boolean sortAscending) {
		setOption(EntityControllerOption.SORT_ASCENDING, sortAscending);
	}

	@Override
	public void clean() {
		try {
			filterMetadata.clean(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			getLogger().error("", e);
		}
	}

	/**
	 * Reset the filter and load again.
	 * 
	 * @return null
	 */
	@UiAction
	public String reset() {
		getFilter().clean();
		return load();
	}

	/**
	 * @return null
	 */
	@UiAction
	public String search() {
		if (isClean()) {
			getUserMessages().info("controller.search.empty");
		} else {
			load();
			getUserMessages().info("controller.search");
		}
		return null;
	}

	@Override
	public boolean isClean() {
		try {
			return filterMetadata.isClean(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			getLogger().error("", e);
		}
		return false;
	}

	@Override
	public void addPagination(QueryBuilder<T> query) {
	}

    public TypedFilterMetadata<T> getFilterMetadata() {
        return filterMetadata;
    }

    public Map<String, TypedFilterMetadata.TypedFilterFieldMetadata> getFilterFields() {
        return filterMetadata.getFilterFields();
    }

}

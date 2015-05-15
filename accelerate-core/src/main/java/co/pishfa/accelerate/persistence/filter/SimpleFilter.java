package co.pishfa.accelerate.persistence.filter;

import co.pishfa.accelerate.entity.common.BaseEntity;
import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.service.handler.PermissionScopeHandler;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authorization.Permission;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Base class for all filters. A filter is a set of conditions that applied to a query for refinement of its results,
 * possible pagination and sorting on a single property. It also handles security issues. It contains the reference to
 * the desired language for handling multilingual issues.
 * 
 * @author Taha Ghasemi
 * 
 */
@MappedSuperclass
public class SimpleFilter<E> extends BaseEntity implements Filter<E> {

	protected String sortOn = null;
	protected boolean sortAscending = true;
	protected int pageStart = 0;
	protected int pageSize = 0;
	protected boolean paginationEnabled = true;
	protected String language;
    protected String viewAction;
    @Transient
	protected Permission viewPermission;

	public SimpleFilter(String viewAction) {
        this.viewAction = viewAction;
	}

	public String getSortOn() {
		return sortOn;
	}

	public void setSortOn(String orderColumn) {
		this.sortOn = orderColumn;
	}

	public boolean isSortAscending() {
		return sortAscending;
	}

	public void setSortAscending(boolean sortAscending) {
		this.sortAscending = sortAscending;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageStart() {
		return pageStart;
	}

	public void setPageStart(int pageStartIndex) {
		this.pageStart = pageStartIndex;
	}

	public boolean hasPagination() {
		return paginationEnabled && pageSize > 0;
	}

	public boolean isPaginationEnabled() {
		return paginationEnabled;
	}

	public void setPaginationEnabled(boolean paginationEnabled) {
		this.paginationEnabled = paginationEnabled;
	}

	public boolean hasOrdering() {
		return getSortOn() != null;
	}

	@Override
	public void clean() {

	}

	@Override
	public boolean isClean() {
		return true;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Adds the conditions to the where part of the query. Note that it is assumed that the query has already where part
	 * with some conditions.
	 */
	@Override
	public void addConditions(QueryBuilder<E> query) {
		if (viewAction != null) {
            Identity identity = Identity.getInstance();
            if(viewPermission == null) {
                viewPermission = identity.findPermission(viewAction);
            }
			PermissionScopeHandler<E> handler = identity.getScopeHandler(viewPermission);
			handler.addConditions(identity, viewPermission, query);
		}
	}

	@Override
	public void addSorting(QueryBuilder<E> query) {
		if (hasOrdering()) {
			query.append(getSortOn()).sortDir(sortAscending);
		}
	}

	@Override
	public void addPagination(QueryBuilder<E> query) {
		if (hasPagination()) {
			query.max(pageSize);
			query.first(pageStart);
		}
	}

}

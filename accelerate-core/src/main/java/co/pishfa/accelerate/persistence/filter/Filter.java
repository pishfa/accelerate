package co.pishfa.accelerate.persistence.filter;

import co.pishfa.accelerate.persistence.query.QueryBuilder;

/**
 * Represents a filtering over a set of entities.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public interface Filter<T> {

	/**
	 * Adds the conditions to the where part of the query. Note that it is assumed that the query has already where part
	 * with some conditions.
	 * 
	 */
	void addConditions(QueryBuilder<T> query);

	void addSorting(QueryBuilder<T> query);

	void addPagination(QueryBuilder<T> query);

	/**
	 * Clean the filter so that all entities will be returned. This is the best place to give init values to your filter
	 * fields.
	 */
	void clean();

	boolean isClean();
}
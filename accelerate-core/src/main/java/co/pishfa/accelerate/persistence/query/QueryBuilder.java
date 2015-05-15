package co.pishfa.accelerate.persistence.query;

import co.pishfa.accelerate.persistence.filter.Filter;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

/**
 * A helper for building string-based queries for entities with specific type. It also ease working with the
 * {@link Filter}. The alias names is fixed to constant 'e'. Samples:
 * <ul>
 * <li>
 * query().select().where(filter).ordered().paged().list()</li>
 * <li>
 * query("select e").fromEntity().append("where e.age > 10").orderBy("e.name asc").max(10).list()</li>
 * <li>
 * query().delete().where("e.id in (:ids)").with("ids", ids).run()</li>
 * </ul>
 * 
 * @author Taha Ghasemi
 * 
 * @param <T>
 *            type of entity which query is run against (although it can be changed if required)
 * 
 */
public interface QueryBuilder<T> {

	/**
	 * Adds the conditions of provided filter.
	 * 
	 * @param filter
	 *            . Can be null.
	 */
	QueryBuilder<T> and(Filter<T> filter);

	/**
	 * Add the ordering from the current filter, if any.
	 */
	QueryBuilder<T> sort();

	/**
	 * Add the ordering besides ordering of the current filter, if any.
	 * 
	 * @param afterFilter
	 *            whether the ordering should be added after or before the ordering of the filter
	 * 
	 */
	QueryBuilder<T> sort(String sortStr, boolean afterFilter);

	/**
	 * Adds order by
	 * 
	 * @return
	 */
	QueryBuilder<T> sortBy();

	/**
	 * Adds order by sortStr
	 * 
	 * @return
	 */
	QueryBuilder<T> sortBy(String sortStr);

	/**
	 * Appends asc or desc based on the ascending parameter.
	 */
	QueryBuilder<T> sortDir(boolean ascending);

	/**
	 * Add the pagination from previously assigned filter, if any, if pagination is enabled inside filter. It internally
	 * sets max and first fields.
	 * 
	 * @return
	 */
	QueryBuilder<T> paged();

	/**
	 * Sets the maximum number of entities to be returned.
	 * 
	 * @return
	 */
	QueryBuilder<T> max(int max);

	/**
	 * Sets the first element to be returned.
	 * 
	 * @return
	 */
	QueryBuilder<T> first(int first);

	/**
	 * @return the query
	 */
	StringBuilder getQuery();

	/**
	 * @return the filter
	 */
	Filter<T> getFilter();

	/**
	 * @return the parameters
	 */
	Map<String, Object> getParams();

	/**
	 * Appends an arbitrary string to the current query.
	 * 
	 * @return
	 */
	QueryBuilder<T> append(String s);

	/**
	 * Sets the value of a parameter.
	 * 
	 * @return
	 */
	QueryBuilder<T> with(String name, Object value);

	/**
	 * Adds select e from entity() e
	 * 
	 * @return
	 */
	QueryBuilder<T> select();

	/**
	 * Adds select e from fromClass e
	 * 
	 * @return
	 */
	QueryBuilder<T> select(String fromClass);

	/**
	 * Adds select count(e) from entity() e
	 * 
	 * @return
	 */
	QueryBuilder<T> selectCount();

	/**
	 * Adds delete from entity() e
	 * 
	 * @return
	 */
	QueryBuilder<T> delete();

	/**
	 * Adds from entity() e
	 * 
	 * @return
	 */
	QueryBuilder<T> fromEntity();

	/**
	 * Adds targetClass e
	 * 
	 * @return
	 */
	QueryBuilder<T> entity();

	/**
	 * Adds entityAlias e
	 * 
	 * @return
	 */
	QueryBuilder<T> entity(String entityAlias);

	/**
	 * Adds where
	 * 
	 * @return
	 */
	QueryBuilder<T> where();

	/**
	 * Adds where 1=1
	 * 
	 * @return
	 */
	QueryBuilder<T> whereTrue();

	/**
	 * Adds where str
	 * 
	 * @return
	 */
	QueryBuilder<T> where(String str);

	/**
	 * Appends and str to the query
	 */
	QueryBuilder<T> and(String str);

	/**
	 * Appends and e.str to the query
	 */
	QueryBuilder<T> andEntityField(String str);

	/**
	 * Appends and e.str = :str to the query + with(str, value)
	 */
	QueryBuilder<T> andEntityFieldEquals(String str, Object value);

	/**
	 * Can be used to append an a path expression of current entity. For example entityField("name.en") appends
	 * e.name.en
	 */
	QueryBuilder<T> entityField(String str);

	/**
	 * Adds where 1=1 and filter.conditions()
	 * 
	 * @return
	 */
	QueryBuilder<T> where(Filter<T> filter);

	/**
	 * @return the resulted query
	 */
	TypedQuery<T> build();

	/**
	 * @return the resulted query
	 */
	<R> TypedQuery<R> build(Class<R> resultClass);

	/**
	 * Builds the query and runs it.
	 * 
	 * @return the single result. throws exception if no result found or no unique result found.
	 */
	T result();

	/**
	 * Builds the query and runs it.
	 * 
	 * @return the single result. throws exception if no result found or no unique result found.
	 */
	<R> R result(Class<R> resultClass);

	/**
	 * Builds the query and runs it.
	 * 
	 * @return the single result, null if no result found or no unique result found
	 */
	T result(boolean nullIfNoResult, boolean nullIfMultipleResult);

	/**
	 * Builds the query and run it as update or delete query.
	 * 
	 * @return
	 */
	int run();

	/**
	 * Builds the query and runs it.
	 * 
	 * @return the list of results.
	 */
	List<T> list();

	/**
	 * Builds the query and runs it.
	 * 
	 * @return the list of results.
	 */
	<R> List<R> list(Class<R> resultClass);

}
package co.pishfa.accelerate.persistence.query;

import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.utility.StrUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * JPA based implementation of {@link QueryBuilder}. It is assumed that every part which want to be added must take care
 * of prefix spaces but it has no business with postfix spaces
 * 
 * @author Taha Ghasemi
 * 
 * @param <T>
 *            type of result
 */
public class JpaQueryBuilder<T extends Entity> implements QueryBuilder<T> {

	private static final Logger log = LoggerFactory.getLogger(JpaQueryBuilder.class);

	private Filter<T> filter;
	private final StringBuilder query;
	private final Map<String, Object> params;
	private final EntityManager entityManager;
	private final Class<T> targetClass;
	private Integer max;
	private Integer first;

	public JpaQueryBuilder(EntityManager entityManager, Class<T> targetClass, String queryHead) {
		this.entityManager = entityManager;
		this.targetClass = targetClass;
		query = new StringBuilder(StrUtils.nullToEmpty(queryHead));
		params = new HashMap<String, Object>();
	}

	@Override
	public QueryBuilder<T> and(Filter<T> filter) {
		if (filter != null) {
			Validate.isTrue(this.filter == null, "At most one filter is acceptable");
			this.filter = filter;
			filter.addConditions(this);
		}
		return this;
	}

	@Override
	public QueryBuilder<T> sort() {
		sortBy();
		addSorting();
		return this;
	}

	protected void addSorting() {
		int length = query.length();
		if (filter != null) {
			filter.addSorting(this);
		}
		if (length == query.length()) { // if no change made by filter
			entityField("id"); // default with id
		}
	}

	@Override
	public QueryBuilder<T> sort(String ordering, boolean afterFilter) {
		if (afterFilter) {
			sort().append(" , ").append(ordering);
		} else {
			sortBy(ordering).append(" , ");
			addSorting();
		}
		return this;
	}

	@Override
	public QueryBuilder<T> sortBy() {
		return append(" order by ");
	}

	@Override
	public QueryBuilder<T> sortBy(String ordering) {
		return sortBy().append(ordering);
	}

	@Override
	public QueryBuilder<T> sortDir(boolean ascending) {
		return append(ascending ? " asc" : " desc");
	}

	@Override
	public QueryBuilder<T> paged() {
		if (filter != null) {
			filter.addPagination(this);
		}
		return this;
	}

	@Override
	public QueryBuilder<T> max(int max) {
		this.max = max;
		return this;
	}

	@Override
	public QueryBuilder<T> first(int first) {
		this.first = first;
		return this;
	}

	public static void setQueryParams(Query q, Map<String, Object> params) {
		StringBuilder output = new StringBuilder("Params: ");
		for (Entry<String, Object> param : params.entrySet()) {
			q.setParameter(param.getKey(), param.getValue());
			output.append(param.getKey()).append(':').append(param.getValue()).append(", ");
		}
		log.info(output.toString());
	}

	/**
	 * @return the query
	 */
	@Override
	public StringBuilder getQuery() {
		return query;
	}

	/**
	 * @return the filter
	 */
	@Override
	public Filter<T> getFilter() {
		return filter;
	}

	/**
	 * @return the params
	 */
	@Override
	public Map<String, Object> getParams() {
		return params;
	}

	@Override
	public QueryBuilder<T> append(String s) {
		query.append(s);
		return this;
	}

	@Override
	public QueryBuilder<T> with(String name, Object value) {
		params.put(name, value);
		return this;
	}

	@Override
	public QueryBuilder<T> select() {
		return append("select e").fromEntity();
	}

	@Override
	public QueryBuilder<T> select(String fromClass) {
		return append("select e from").entity(fromClass);
	}

	@Override
	public QueryBuilder<T> selectCount() {
		return append("select count(e)").fromEntity();
	}

	@Override
	public QueryBuilder<T> delete() {
		return append("delete").fromEntity();
	}

	@Override
	public QueryBuilder<T> fromEntity() {
		return append(" from").entity();
	}

	@Override
	public QueryBuilder<T> entity() {
		return entity(targetClass.getName());
	}

	@Override
	public QueryBuilder<T> entity(String entityAlias) {
		return append(" ").append(entityAlias).append(" e");
	}

	@Override
	public QueryBuilder<T> where() {
		return append(" where");
	}

	@Override
	public QueryBuilder<T> whereTrue() {
		return append(" where 1=1");
	}

	@Override
	public QueryBuilder<T> where(String str) {
		return append(" where ").append(str);
	}

	@Override
	public QueryBuilder<T> and(String str) {
		return append(" and ").append(str);
	}

	@Override
	public QueryBuilder<T> andEntityField(String str) {
		return and("e." + str);
	}

	@Override
	public QueryBuilder<T> andEntityFieldEquals(String str, Object value) {
		String paramName = str.replace('.', '_');
		return andEntityField(str).append(" = :").append(paramName).with(paramName, value);
	}

	@Override
	public QueryBuilder<T> entityField(String str) {
		return append("e.").append(str);
	}

	@Override
	public QueryBuilder<T> where(Filter<T> filter) {
		return whereTrue().and(filter);
	}

	@Override
	public TypedQuery<T> build() {
		return build(targetClass);
	}

	@Override
	public <R> TypedQuery<R> build(Class<R> resultClass) {
		String queryStr = query.toString();
		log.info(queryStr);
		TypedQuery<R> q = entityManager.createQuery(queryStr, resultClass);
		setQueryParams(q, params);
		if (max != null) {
			q.setMaxResults(max);
		}
		if (first != null) {
			q.setFirstResult(first);
		}
		return q;
	}

    public Query buildUnTyped() {
        String queryStr = query.toString();
        log.info(queryStr);
        Query q = entityManager.createQuery(queryStr);
        setQueryParams(q, params);
        if (max != null) {
            q.setMaxResults(max);
        }
        if (first != null) {
            q.setFirstResult(first);
        }
        return q;
    }

	@Override
	public T result() {
		return build().getSingleResult();
	}

	@Override
	public <R> R result(Class<R> resultClass) {
		return build(resultClass).getSingleResult();
	}

	@Override
	public T result(boolean nullIfNoResult, boolean nullIfMultipleResult) {
		try {
			return result();
		} catch (NoResultException nre) {
			if (nullIfNoResult) {
				return null;
			}
			throw nre;
		} catch (NonUniqueResultException nue) {
			if (nullIfMultipleResult) {
				return null;
			}
			throw nue;
		}
	}

	@Override
	public int run() {
		return buildUnTyped().executeUpdate();
	}

	@Override
	public List<T> list() {
		return build().getResultList();
	}

	@Override
	public <R> List<R> list(Class<R> resultClass) {
		return build(resultClass).getResultList();
	}

}

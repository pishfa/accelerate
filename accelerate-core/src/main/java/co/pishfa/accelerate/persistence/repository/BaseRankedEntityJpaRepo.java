package co.pishfa.accelerate.persistence.repository;

import org.apache.deltaspike.jpa.api.transaction.Transactional;

import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.accelerate.entity.common.RankedEntity;

import java.util.List;

/**
 * Note: if filter contains join, hiberante is not support join in updates and it should be converted to subqueries
 * 
 * @author Taha Ghasemi
 * 
 */
public abstract class BaseRankedEntityJpaRepo<T extends RankedEntity<K>,K> extends BaseJpaRepo<T, K>
		implements RankedEntityRepo<T,K> {

	public BaseRankedEntityJpaRepo() {
	}

	public BaseRankedEntityJpaRepo(Class<T> entityClass, Class<K> keyClass) {
		super(entityClass,keyClass);
	}

	@Override
	public T findByRank(Filter<T> filter, int rank) {
		return query("").select().where(filter).andEntityField("e.rank = :rank").with("rank", rank).result();
	}

	public List<T> findAllOrderByRank() {
		return query("").select().sortBy("e.rank").list();
	}

	@Override
	@Transactional
	public void increment(Filter<T> filter, int fromRank) {
		head(filter, "set e.rank = e.rank + 1").and("e.rank >= :rank").with("rank", fromRank).run();
		getEntityManager().clear();
	}

	@Override
	@Transactional
	public void increment(Filter<T> filter, int fromRank, int toRank) {
		head(filter, "set e.rank = e.rank + 1").and("e.rank >= :rankStart and e.rank < :rankEnd")
				.with("rankStart", fromRank).with("rankEnd", toRank).run();
		getEntityManager().clear();
	}

	@Override
	@Transactional
	public void decrement(Filter<T> filter, int fromRank) {
		head(filter, "set e.rank = e.rank - 1").and("e.rank >= :rank").with("rank", fromRank).run();
		getEntityManager().clear();
	}

	@Override
	@Transactional
	public void decrement(Filter<T> filter, int fromRank, int toRank) {
		head(filter, "set e.rank = e.rank - 1").and("e.rank >= :rankStart and e.rank < :rankEnd")
				.with("rankStart", fromRank).with("rankEnd", toRank).run();
		getEntityManager().clear();
	}

	protected QueryBuilder<T> head(Filter<T> filter, String head) {
		return query("update ").entity().append(" ").append(head).where(filter);
	}

	@Override
	@Transactional
	public T setRank(Filter<T> filter, T entity, int rank) {
		int eRank = entity.getRank();
		if (rank > eRank) {
			decrement(filter, eRank + 1, rank + 1);
		} else if (rank < eRank) {
			increment(filter, rank, eRank);
		}
		entity.setRank(rank);
		return edit(entity);
	}

	@Override
	public int maxRank(Filter<T> filter) {
		Integer res = query("select max(e.rank)").fromEntity().where(filter).result(Integer.class);
		return res==null?0:res.intValue();
	}

}

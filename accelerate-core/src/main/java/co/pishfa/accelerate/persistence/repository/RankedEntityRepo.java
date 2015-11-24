/**
 * 
 */
package co.pishfa.accelerate.persistence.repository;

import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.service.RankedEntityService;
import co.pishfa.accelerate.entity.common.RankedEntity;

/**
 * @author Taha Ghasemi
 * 
 * @param <T>
 */
public interface RankedEntityRepo<T extends RankedEntity<K>,K> extends EntityRepository<T,K>, RankedEntityService<T,K> {

	T findByRank(Filter<T> filter, int rank);

}
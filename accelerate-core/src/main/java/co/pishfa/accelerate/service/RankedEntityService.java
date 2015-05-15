package co.pishfa.accelerate.service;

import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.entity.common.RankedEntity;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public interface RankedEntityService<T extends RankedEntity<K>,K> extends EntityService<T,K> {

	T setRank(Filter<T> filter, T entity, int rank);

	int maxRank(Filter<T> filter);

}

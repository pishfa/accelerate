package co.pishfa.accelerate.persistence.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.pishfa.accelerate.persistence.query.QueryBuilder;

import javax.persistence.Transient;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class TypedFilter<E> extends SimpleFilter<E> {

    @Transient
	private static final Logger log = LoggerFactory.getLogger(TypedFilter.class);
    @Transient
	private static TypedFilterMetadata metadata;

	public TypedFilter(String viewAction) {
		super(viewAction);
		if (metadata == null) {
			metadata = new TypedFilterMetadata(getClass());
		}
	}

	@Override
	public void addConditions(QueryBuilder<E> query) {
        super.addConditions(query);
		try {
			metadata.addConditions(query, this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("", e);
		}
	}

	@Override
	public void clean() {
		try {
			metadata.clean(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("", e);
		}
	}

	@Override
	public boolean isClean() {
		try {
			return metadata.isClean(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("", e);
		}
		return false;
	}

}

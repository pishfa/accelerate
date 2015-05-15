package co.pishfa.accelerate.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 * @param <K>
 * @param <V>
 */
public class ConcurrentCache<K, V> extends MapWrappedCache<K, V> {

	private static final long serialVersionUID = 1L;

	public ConcurrentCache() {
		super(new ConcurrentHashMap<K, CacheValue<V>>());
	}

}

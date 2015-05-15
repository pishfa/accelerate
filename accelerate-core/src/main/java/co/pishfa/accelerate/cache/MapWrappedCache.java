package co.pishfa.accelerate.cache;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.Validate;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 * @param <K>
 * @param <V>
 */
public class MapWrappedCache<K, V> implements Cache<K, V>, Serializable {

	private static final long serialVersionUID = 1L;
	private final Map<K, CacheValue<V>> delegate;

	public MapWrappedCache(Map<K, CacheValue<V>> delegate) {
		Validate.notNull(delegate);
		this.delegate = delegate;
	}

	@Override
	public V get(K key) {
		CacheValue<V> value = delegate.get(key);
		if (value == null) {
			throw new IllegalArgumentException("Key is not found in the cache " + key);
		} else {
			return value.getValue();
		}
	}

	@Override
	public V getIfPresent(K key) {
		CacheValue<V> value = delegate.get(key);
		return value == null ? null : value.getValue();
	}

	@Override
	public void put(K key, V value) {
		delegate.put(key, new CacheValue<V>(value));
	}

	@Override
	public void remove(K key) {
		delegate.remove(key);
	}

	@Override
	public void removeAll() {
		delegate.clear();
	}

}

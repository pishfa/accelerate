package co.pishfa.accelerate.cache;

import java.util.Map;

import org.apache.commons.lang3.Validate;

/**
 * Note: it is not thread safe
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class MapWithNullCache<K, V> implements Cache<K, V> {

	private final Map<K, V> delegate;
	private static Object nullObj = new Object();

	public MapWithNullCache(Map<K, V> delegate) {
		Validate.notNull(delegate);
		this.delegate = delegate;
	}

	@Override
	public V get(K key) {
		V res = delegate.get(key);
		if (res == nullObj) {
			return null;
		} else if (res != null) {
			return res;
		} else {
			throw new IllegalArgumentException("Key is not found in the cache " + key);
		}
	}

	@Override
	public V getIfPresent(K key) {
		V res = delegate.get(key);
		if (res == nullObj) {
			return null;
		} else {
			return res;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void put(K key, V value) {
		if (value == null) {
			value = (V) nullObj;
		}
		delegate.put(key, value);
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

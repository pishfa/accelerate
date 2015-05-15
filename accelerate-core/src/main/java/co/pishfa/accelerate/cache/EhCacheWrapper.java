/**
 * 
 */
package co.pishfa.accelerate.cache;

import net.sf.ehcache.Element;

import org.apache.commons.lang3.Validate;

import co.pishfa.accelerate.utility.CommonUtils;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class EhCacheWrapper<K, V> implements Cache<K, V> {

	private final net.sf.ehcache.Cache delegate;

	public EhCacheWrapper(net.sf.ehcache.Cache delegate) {
		Validate.notNull(delegate);
		this.delegate = delegate;
	}

	@Override
	public V get(K key) {
		Element element = delegate.get(key);
		if (element != null) {
			return CommonUtils.cast(element.getValue());
		}
		throw new IllegalArgumentException("No cache entry with key " + key);
	}

	@Override
	public V getIfPresent(K key) {
		Element element = delegate.get(key);
		if (element != null) {
			return CommonUtils.cast(element.getValue());
		}
		return null;
	}

	@Override
	public void put(K key, V value) {
		delegate.put(new Element(key, value));
	}

	@Override
	public void remove(K key) {
		delegate.remove(key);
	}

	@Override
	public void removeAll() {
		delegate.removeAll();
	}

}

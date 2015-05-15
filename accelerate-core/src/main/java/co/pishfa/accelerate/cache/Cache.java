/**
 * 
 */
package co.pishfa.accelerate.cache;

import co.pishfa.accelerate.cdi.Veto;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Veto
public interface Cache<K, V> {

	/**
	 * throws IllegalArgumentException if the given key is not present.
	 */
	V get(K key);

	/**
	 * @return null in case of the key is not present or the actual value that is cached is null
	 */
	V getIfPresent(K key);

	/**
	 * @param key
	 * @param value
	 *            can be null
	 */
	void put(K key, V value);

	void remove(K key);

	void removeAll();

}

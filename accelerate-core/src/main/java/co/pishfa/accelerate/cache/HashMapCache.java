package co.pishfa.accelerate.cache;

import java.util.HashMap;

/**
 * Note: it is not thread safe
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class HashMapCache<K, V> extends MapWithNullCache<K, V> {

	public HashMapCache() {
		super(new HashMap<K, V>());
	}

}

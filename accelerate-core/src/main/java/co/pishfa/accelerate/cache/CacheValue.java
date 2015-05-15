package co.pishfa.accelerate.cache;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 * @param <V>
 */
public class CacheValue<V> {

	private final V value;

	/**
	 * @param value
	 */
	public CacheValue(V value) {
		super();
		this.value = value;
	}

	public V getValue() {
		return value;
	}

}

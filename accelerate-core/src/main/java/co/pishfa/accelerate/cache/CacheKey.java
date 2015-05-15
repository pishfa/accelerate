package co.pishfa.accelerate.cache;

import java.util.Arrays;

/**
 * Contains a list of objects that together acts as a key.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class CacheKey {

	private final Object[] objects;

	/**
	 * @param objects
	 */
	public CacheKey(Object[] objects) {
		this.objects = objects;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(objects);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CacheKey other = (CacheKey) obj;
		if (!Arrays.equals(objects, other.objects)) {
			return false;
		}
		return true;
	}

}

/**
 * 
 */
package co.pishfa.accelerate.config;

import java.util.Hashtable;

import co.pishfa.accelerate.utility.CommonUtils;

/**
 * In-memory {@link Hashtable} based implementation of {@link Config}.
 * It also has the capability to be merged with another {@link HashTableConfig}.
 * 
 * @author Taha Ghasemi
 * 
 */
public class HashTableConfig extends AbstractConfig {

	private final Hashtable<Object, Object> entries = new Hashtable<>();

	@Override
	public <T> T getObject(String key) {
		return CommonUtils.cast(entries.get(key));
	}

	@Override
	public void setObject(String key, Object value) {
		entries.put(key, value);
	}

	public HashTableConfig addAll(HashTableConfig src) {
		this.entries.putAll(src.entries);
		return this;
	}

}

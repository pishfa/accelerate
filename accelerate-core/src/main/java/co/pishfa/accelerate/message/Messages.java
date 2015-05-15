/**
 * 
 */
package co.pishfa.accelerate.message;

import java.util.Collection;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import co.pishfa.accelerate.cdi.Veto;

/**
 * Exposes one or several resource bundles as a map. If the provided key is not available it returns the key itself.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Veto
public class Messages implements Map<String, String> {

	private final ResourceBundle bundle;

	public Messages(ResourceBundle bundle) {
		super();
		this.bundle = bundle;
	}

	public Messages() {
		this.bundle = null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return bundle.containsKey(String.valueOf(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public String get(Object key) {
		try {
			return bundle.getString(String.valueOf(key));
		} catch (MissingResourceException e) {
			return String.valueOf(key);
		}
	}

	@Override
	public String put(String key, String value) {
		return null;
	}

	@Override
	public String remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
	}

	@Override
	public void clear() {
	}

	@Override
	public Set<String> keySet() {
		return null;
	}

	@Override
	public Collection<String> values() {
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return null;
	}

}

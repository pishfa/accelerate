package co.pishfa.accelerate.cache;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.Validate;

import co.pishfa.accelerate.exception.NotSupportedException;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class HttpSessionCache implements Cache<String, Object> {

	private final HttpSession session;

	public HttpSessionCache(HttpSession session) {
		Validate.notNull(session);
		this.session = session;
	}

	@Override
	public Object get(String key) {
		return session.getAttribute(key);
	}

	@Override
	public Object getIfPresent(String key) {
		return session.getAttribute(key);
	}

	@Override
	public void put(String key, Object value) {
		session.setAttribute(key, value);
	}

	@Override
	public void remove(String key) {
		session.removeAttribute(key);
	}

	@Override
	public void removeAll() {
		throw new NotSupportedException();
	}

}

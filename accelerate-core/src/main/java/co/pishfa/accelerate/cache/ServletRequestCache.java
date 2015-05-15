package co.pishfa.accelerate.cache;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.Validate;

import co.pishfa.accelerate.exception.NotSupportedException;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class ServletRequestCache implements Cache<String, Object> {

	private final ServletRequest request;

	public ServletRequestCache(ServletRequest request) {
		Validate.notNull(request);
		this.request = request;
	}

	@Override
	public Object get(String key) {
		return request.getAttribute(key);
	}

	@Override
	public Object getIfPresent(String key) {
		return request.getAttribute(key);
	}

	@Override
	public void put(String key, Object value) {
		request.setAttribute(key, value);
	}

	@Override
	public void remove(String key) {
		request.removeAttribute(key);
	}

	@Override
	public void removeAll() {
		throw new NotSupportedException();
	}

}

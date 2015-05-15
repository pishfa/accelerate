package co.pishfa.accelerate.cache;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.Validate;

import co.pishfa.accelerate.exception.NotSupportedException;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class ServletContextCache implements Cache<String, Object> {

	private final ServletContext servletContext;

	public ServletContextCache(ServletContext servletContext) {
		Validate.notNull(servletContext);
		this.servletContext = servletContext;
	}

	@Override
	public Object get(String key) {
		return servletContext.getAttribute(key);
	}

	@Override
	public Object getIfPresent(String key) {
		return servletContext.getAttribute(key);
	}

	@Override
	public void put(String key, Object value) {
		servletContext.setAttribute(key, value);
	}

	@Override
	public void remove(String key) {
		servletContext.removeAttribute(key);
	}

	@Override
	public void removeAll() {
		throw new NotSupportedException();
	}

}

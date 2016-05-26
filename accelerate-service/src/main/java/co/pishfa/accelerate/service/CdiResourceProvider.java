/**
 * 
 */
package co.pishfa.accelerate.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Singleton;

import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.message.Message;
import org.apache.deltaspike.core.api.literal.AnyLiteral;

import co.pishfa.accelerate.cdi.CdiUtils;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class CdiResourceProvider implements ResourceProvider {

	private final Class<?> resourceClass;
	private final boolean singleton;

	public CdiResourceProvider(Class<?> resourceClass) {
		this.resourceClass = resourceClass;
		BeanManager manager = CdiUtils.getBeanManager();
		Bean<?> bean = manager.resolve(manager.getBeans(resourceClass, new AnyLiteral()));
		singleton = bean.getScope().equals(ApplicationScoped.class) || bean.getScope().equals(Singleton.class);
	}

	public Object getInstance(Message m) {
		return CdiUtils.getInstance(resourceClass);
	}

	public void releaseInstance(Message m, Object o) {
	}

	public Class<?> getResourceClass() {
		return resourceClass;
	}

	public boolean isSingleton() {
		return singleton;
	}

}

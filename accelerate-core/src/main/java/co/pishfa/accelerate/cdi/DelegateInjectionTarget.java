/**
 * 
 */
package co.pishfa.accelerate.cdi;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * An injection target that only delegates to another injection target.
 * 
 * @author Taha Ghasemi
 * 
 */
public class DelegateInjectionTarget<T> implements InjectionTarget<T> {

	private final InjectionTarget<T> delegate;

	/**
	 * @param delegate
	 */
	public DelegateInjectionTarget(InjectionTarget<T> delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public T produce(CreationalContext<T> ctx) {
		return delegate.produce(ctx);
	}

	@Override
	public void dispose(T instance) {
		delegate.dispose(instance);
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return delegate.getInjectionPoints();
	}

	@Override
	public void inject(T instance, CreationalContext<T> ctx) {
		delegate.inject(instance, ctx);
	}

	@Override
	public void postConstruct(T instance) {
		delegate.postConstruct(instance);
	}

	@Override
	public void preDestroy(T instance) {
		delegate.preDestroy(instance);
	}

}

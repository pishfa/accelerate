/**
 * 
 */
package co.pishfa.accelerate.config.cdi;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionTarget;

import co.pishfa.accelerate.cdi.DelegateInjectionTarget;

/**
 * @author Taha Ghasemi
 * 
 */
public class ConfiguratoinInjectionTarget<T> extends DelegateInjectionTarget<T> {

	/**
	 * @param delegate
	 */
	public ConfiguratoinInjectionTarget(InjectionTarget<T> delegate) {
		super(delegate);
	}

	@Override
	public void inject(T instance, CreationalContext<T> ctx) {
		super.inject(instance, ctx);

	}

}

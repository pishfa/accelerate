/**
 * 
 */
package co.pishfa.accelerate.service;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class AccelerateBusFactory extends CXFBusFactory {

	@Override
	public Bus createBus() {
		if (defaultBus == null) {
			return super.createBus();
		} else {
			possiblySetDefaultBus(defaultBus);
			return defaultBus;
		}
	}

}

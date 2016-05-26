/**
 * 
 */
package co.pishfa.accelerate.service;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;

import java.util.Arrays;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class AccelerateBusFactory extends CXFBusFactory {

	@Override
	public Bus createBus() {
		Bus bus = getBus();
		//bus.setProperty("jaxrs.providers", Arrays.asList(JacksonJsonProvider.class.getName()));
		return bus;
	}

	protected Bus getBus() {
		if (defaultBus == null) {
			Bus bus = super.createBus();
			possiblySetDefaultBus(bus);
			return bus;
		} else {
			return defaultBus;
		}
	}

}

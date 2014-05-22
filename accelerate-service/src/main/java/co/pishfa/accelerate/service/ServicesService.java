/**
 * 
 */
package co.pishfa.accelerate.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.core.FrameworkStartedEvent;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@ApplicationScoped
public class ServicesService {

	public void register(@Observes FrameworkStartedEvent event) {
		for (Class<?> service : ServiceExtention.getWebServices()) {
			ServerFactoryBean serverFactory = new JaxWsServerFactoryBean();
			serverFactory.setServiceBean(CdiUtils.getInstance(service));
			serverFactory.setServiceClass(service);
			serverFactory.setAddress("/" + service.getSimpleName());
			serverFactory.setDataBinding(new JAXBDataBinding());
			serverFactory.setBus(AccelerateBusFactory.getDefaultBus());
			serverFactory.create();
		}

		for (Class<?> resource : ServiceExtention.getResources()) {
			JAXRSServerFactoryBean serverFactory = new JAXRSServerFactoryBean();
			serverFactory.setResourceProvider(new CdiResourceProvider(resource));
			serverFactory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
			serverFactory.setAddress("/" + resource.getSimpleName());
			serverFactory.setBus(AccelerateBusFactory.getDefaultBus());
			serverFactory.create();
		}
	}
}

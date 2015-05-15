/**
 * 
 */
package co.pishfa.accelerate.core;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.config.Config;
import co.pishfa.accelerate.config.cdi.ConfigService;
import co.pishfa.accelerate.config.cdi.Global;
import co.pishfa.accelerate.initializer.DbInitListener;
import co.pishfa.accelerate.initializer.api.Initializer;
import co.pishfa.accelerate.initializer.api.InitializerFactory;
import co.pishfa.accelerate.log.Logged;
import co.pishfa.accelerate.resource.ResourceUtils;
import co.pishfa.accelerate.service.Service;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Taha Ghasemi
 * 
 */
@Service
public class FrameworkService {

	@Inject
	private Logger log;

	@Inject
	private ConfigService configService;

	@Inject
	private Event<FrameworkStartedEvent> frameworkStartedEvent;

	@Inject
	private Event<FrameworkPreStartedEvent> frameworkPreStartedEvent;

	@Inject
	private Event<DbInitializedEvent> dbInitEvent;

	@Inject
	private Event<ConfigAppliedEvent> configAppliedEvent;

	@Inject
    @Global
	private Config config;

	@Inject
	private ContextControl contextControl;

	public static FrameworkService getInstance() {
		return CdiUtils.getInstance(FrameworkService.class);
	}

    @Logged
	public void start() {
		log.info("Staring accelerator framework ..............................");
		try {
			contextControl.startContext(RequestScoped.class);
			frameworkPreStartedEvent.fire(new FrameworkPreStartedEvent());
		} catch (Exception e) {
			log.error("Exception during startup of the framework", e);
		} finally {
			contextControl.stopContext(RequestScoped.class);
		}
	}

	protected void initConfigAndDb(@Observes final FrameworkPreStartedEvent event, final DbInitListener listener)
			throws Exception {
		boolean hasConfiguration = configService.loadConfiguration();
		if (!hasConfiguration) {
            getInstance().initDb(false, listener);
            configService.reloadConfiguration(); //TODO: reload, better design needed
        } else {
			boolean isIncrementalInit = config.getBoolean("init.incremental");
			if (isIncrementalInit) {
				getInstance().initDb(true, listener);
			}
		}
		configAppliedEvent.fire(new ConfigAppliedEvent(config));
		frameworkStartedEvent.fire(new FrameworkStartedEvent());
	}

	@Transactional
	public void initDb(final boolean isIncrementalInit, final DbInitListener listener) throws Exception, IOException {
		InitializerFactory factory = new InitializerFactory().entityClasses(FrameworkExtension.getAnnotatedEntities())
				.incremental(isIncrementalInit).autoAnchor(true).key("name");
		Map<String, Object> contextVars = new HashMap<>(3);
		contextVars.put("config", config);
		Initializer initializer = factory.create(listener, contextVars);
		try (InputStream stream = ResourceUtils.getResourceAsStream("init.xml")) {
			initializer.read(stream, false);
		}
		dbInitEvent.fire(new DbInitializedEvent());
	}

}

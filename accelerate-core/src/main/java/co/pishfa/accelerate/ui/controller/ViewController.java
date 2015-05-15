package co.pishfa.accelerate.ui.controller;

import co.pishfa.accelerate.log.Logged;
import co.pishfa.accelerate.log.LoggerHolder;
import co.pishfa.accelerate.reflection.ReflectionUtils;
import co.pishfa.accelerate.ui.phase.PhaseId;
import co.pishfa.accelerate.ui.phase.UiPhaseAction;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Base controller which might have several child controllers. It manages parent-child relationships between controllers.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class ViewController implements LoggerHolder, Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ViewController.class);

	@Inject
	transient private Logger log;

	private ViewController parentController;

	/**
	 * Attention: might be null. View lifecycle events and parameter injection automatically are passed to these
	 * controllers. You may use {@link #addChildController(ViewController)} to safely add controllers to this.
	 */
	private List<ViewController> childControllers;

	public ViewController() {
	}

	@PostConstruct
	private void onPostConstruct() {
		init();
		// Find declared child controllers and add them.
		for (Field field : ReflectionUtils.getAllFieldsAnnotatedWith(getClass(), UiChildController.class)) {
			UiChildController childController = field.getAnnotation(UiChildController.class);
			if (childController != null && ViewController.class.isAssignableFrom(field.getType())) {
				try {
					if (!field.isAccessible()) {
						field.setAccessible(true);
					}
					addChildController((ViewController) field.get(this));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					getLogger().error("", e);
				}
			}
		}
	}

	/**
	 * Do any initialization stuff that do not depend on view lifecycle here. The dependency injection is done at this
	 * step, but parameters are not injected yet. Also no UiPhaseActions is executed yet.
	 */
	@Logged
	protected void init() {
		getLogger().info("init");
	}

	/**
	 * Calls for the first time when the current is about to be loaded (not on post-backs)
	 */
	@UiPhaseAction(PhaseId.RESTORE_VIEW)
	@Logged
	protected void onViewLoaded() throws Exception {
		getLogger().info("onViewLoaded");
	}

	@Override
	public Logger getLogger() {
		if (log != null)
			return log;
		return LOG;
	}

	protected void addChildController(ViewController controller) {
		Validate.notNull(controller);

		if (getChildControllers() == null) {
			setChildControllers(new ArrayList<ViewController>());
		}
		getChildControllers().add(controller);
		controller.setParentController(this);
	}

	public void setParentController(ViewController parentController) {
		this.parentController = parentController;
	}

	public ViewController getParentController() {
		return parentController;
	}

	public List<ViewController> getChildControllers() {
		return childControllers;
	}

	protected void setChildControllers(List<ViewController> childControllers) {
		this.childControllers = childControllers;
	}

}

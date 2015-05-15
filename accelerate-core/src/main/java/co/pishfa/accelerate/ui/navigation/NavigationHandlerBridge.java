package co.pishfa.accelerate.ui.navigation;

import java.util.Map;
import java.util.Set;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class NavigationHandlerBridge extends ConfigurableNavigationHandler {

	private final ConfigurableNavigationHandler wrapped;
	private final AccelerateNavigationHandler uiCdiNavigationHandler;

	public NavigationHandlerBridge(ConfigurableNavigationHandler wrapped) {
		this.wrapped = wrapped;
		uiCdiNavigationHandler = AccelerateNavigationHandler.getInstance();
	}

	@Override
	public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
		return wrapped.getNavigationCase(context, fromAction, uiCdiNavigationHandler.translate(outcome));
	}

	@Override
	public Map<String, Set<NavigationCase>> getNavigationCases() {
		return wrapped.getNavigationCases();
	}

	@Override
	public void handleNavigation(FacesContext context, String fromAction, String outcome) {
		wrapped.handleNavigation(context, fromAction, uiCdiNavigationHandler.translate(outcome));
	}

}

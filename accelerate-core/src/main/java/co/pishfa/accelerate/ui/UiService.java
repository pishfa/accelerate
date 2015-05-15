/**
 * 
 */
package co.pishfa.accelerate.ui;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.service.Service;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Map;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Service
public class UiService implements Serializable {

	public static UiService getInstance() {
		return CdiUtils.getInstance(UiService.class);
	}

	// From: https://cwiki.apache.org/confluence/display/MYFACES/Access+FacesContext+From+Servlet
	public void createContext(HttpServletRequest request, HttpServletResponse response) {
		FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		FacesContext facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(), request,
				response, lifecycle);
		InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);
	}

	private abstract static class InnerFacesContext extends FacesContext {
		protected static void setFacesContextAsCurrentInstance(FacesContext facesContext) {
			FacesContext.setCurrentInstance(facesContext);
		}
	}

	public void removeContext() {
		InnerFacesContext.setFacesContextAsCurrentInstance(null);
	}

	@Produces
	@RequestScoped
	/* It is not serializable */
	public ServletContext getServletContext() {
		return UiUtils.getServletContext();
	}

	@Produces
	@RequestScoped
	public FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	@Produces
	@RequestScoped
	/* It is not serializable */
	public HttpSession getSession() {
		return UiUtils.getSession();
	}

	@Produces
	@RequestScoped
	public HttpServletRequest getRequest() {
		return UiUtils.getRequest();
	}

	@Produces
	@RequestScoped
	public HttpServletResponse getResponse() {
		return UiUtils.getResponse();
	}

	@Produces
	@RequestScoped
	public Map<String, Object> getView() {
		return UiUtils.getViewMap();
	}

}

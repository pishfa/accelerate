package co.pishfa.accelerate.ui;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 
 * Utility methods to be used in the presentation layer.
 * 
 * @author Taha Ghasemi
 */
public class UiUtils {

	public static ServletContext getServletContext() {
		return (ServletContext) getExternalContext().getContext();
	}

	public static ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance() == null? null : FacesContext.getCurrentInstance().getExternalContext();
	}

	public static HttpSession getSession() {
		return getExternalContext() == null ? null : (HttpSession) getExternalContext().getSession(true);
	}

	public static HttpServletRequest getRequest() {
		return getExternalContext() == null ? null : (HttpServletRequest) getExternalContext().getRequest();
	}

	public static HttpServletResponse getResponse() {
		return getExternalContext() == null ? null : (HttpServletResponse) getExternalContext().getResponse();
	}

	public static UIViewRoot getViewRoot() {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context != null) {
			return context.getViewRoot();
		}

		return null;
	}

	public static String getViewId() {
		UIViewRoot root = getViewRoot();
		return root == null ? null : root.getViewId();
	}

	public static Map<String, Object> getViewMap() {
		UIViewRoot viewRoot = getViewRoot();

		if (viewRoot != null) {
			return viewRoot.getViewMap(true);
		}

		return null;
	}

}

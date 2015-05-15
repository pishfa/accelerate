/**
 * 
 */
package co.pishfa.accelerate.context;

import co.pishfa.accelerate.ui.UiService;
import co.pishfa.accelerate.utility.CommonUtils;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Taha Ghasemi
 * 
 */
@SessionScoped
public class SessionContext implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UiService uiService;

	public <T> T get(String key, Class<T> valueClass) {
		if (uiService.getSession() != null) {
			return CommonUtils.cast(uiService.getSession().getAttribute(key));
		}
		return null;
	}

	public void set(String key, Object value) {
		uiService.getSession().setAttribute(key, value);
	}

}

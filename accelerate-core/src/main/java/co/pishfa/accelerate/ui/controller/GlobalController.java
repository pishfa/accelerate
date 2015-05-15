package co.pishfa.accelerate.ui.controller;

import java.io.Serializable;

import co.pishfa.accelerate.portal.service.PageMetadataService;
import co.pishfa.accelerate.ui.navigation.AccelerateNavigationHandler;
import co.pishfa.accelerate.ui.param.UiParam;

/**
 * Contains fields and actions that shared across all pages.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@UiController(PageMetadataService.GLOBAL)
public class GlobalController implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String returnView;

	public String getReturnView() {
		return returnView;
	}

	@UiParam(AccelerateNavigationHandler.RETURN_VIEW)
	public void setReturnView(String returnView) {
		this.returnView = returnView;
	}

}

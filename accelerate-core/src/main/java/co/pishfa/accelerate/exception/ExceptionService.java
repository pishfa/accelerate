package co.pishfa.accelerate.exception;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.config.Config;
import co.pishfa.accelerate.config.cdi.Global;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@ApplicationScoped
public class ExceptionService {

	public static class UiExceptionData {

		public String message;
		public String page;

		public UiExceptionData(String message, String page) {
			this.message = message;
			this.page = page;
		}

	}

	@Inject
    @Global
	private Config config;

	public static ExceptionService getInstance() {
		return CdiUtils.getInstance(ExceptionService.class);
	}

	public UiExceptionData getUiException(Throwable exception) {
		if (exception == null) {
			return null;
		}
		String base = "ui.exceptions." + exception.getClass().getName();
		String message = config.getString(base + ".message");
		String page = config.getString(base + ".page");
		if (page != null || message != null) {
			return new UiExceptionData(message, page);
		}
		UiException uiException = exception.getClass().getAnnotation(UiException.class);
		if (uiException != null) {
			return new UiExceptionData(uiException.message(), uiException.page());
		}
		return null;
	}

}

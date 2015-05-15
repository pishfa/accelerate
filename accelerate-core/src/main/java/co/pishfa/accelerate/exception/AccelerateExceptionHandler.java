package co.pishfa.accelerate.exception;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;

import co.pishfa.accelerate.config.cdi.ConfigService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.pishfa.accelerate.config.Config;
import co.pishfa.accelerate.exception.ExceptionService.UiExceptionData;
import co.pishfa.accelerate.message.UserMessages;
import co.pishfa.accelerate.utility.StrUtils;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class AccelerateExceptionHandler extends ExceptionHandlerWrapper {

	private static final Logger log = LoggerFactory.getLogger(AccelerateExceptionHandler.class);

	private final ExceptionHandler wrapped;
	private final Config config;

	public AccelerateExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
		config = ConfigService.getInstance().getConfig();
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}

	@Override
	public void handle() throws FacesException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		UserMessages userMessages = UserMessages.getInstance();

		boolean isProduction = facesContext.isProjectStage(ProjectStage.Production);
		boolean inError = false;
		boolean navigatedToPage = false;
		// handle the ui exceptions
		// log other exceptions + in production, don't allow them to be thrown just redirect to error current
		for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
			ExceptionQueuedEvent event = i.next();
			Throwable exception = event.getContext().getException();
			UiExceptionData uiException = ExceptionService.getInstance().getUiException(exception);
			if (uiException == null) {
				uiException = ExceptionService.getInstance().getUiException(ExceptionUtils.getRootCause(exception));
			}
			if (uiException != null) {
				if (!StrUtils.isEmpty(uiException.message)) {
					userMessages.error(uiException.message);
				}
				if (!StrUtils.isEmpty(uiException.page) && !navigatedToPage) {
					Faces.navigate(uiException.page);
					navigatedToPage = true; // can not handle any more errors
				}
				i.remove();
			} else {
				inError = true;
				log.error("", exception);
				if (isProduction) {
					i.remove();
				}
			}
		}
		if (inError && isProduction) {
			userMessages.error(config.getString("ui.exceptions.not-handeled.message"));
			Faces.navigate(config.getString("ui.exceptions.not-handeled.page"));
		} else {
			wrapped.handle();
		}
	}

}

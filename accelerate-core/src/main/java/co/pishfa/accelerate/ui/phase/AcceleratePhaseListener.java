package co.pishfa.accelerate.ui.phase;

import co.pishfa.accelerate.convert.Converter;
import co.pishfa.accelerate.convert.Unchecked;
import co.pishfa.accelerate.portal.entity.Page;
import co.pishfa.accelerate.portal.service.PageMetadata;
import co.pishfa.accelerate.portal.service.PageMetadata.PageControllerMetadata;
import co.pishfa.accelerate.portal.service.PageMetadataService;
import co.pishfa.accelerate.ui.param.UiParam;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.entity.audit.AuditLevel;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.service.AuditService;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@ApplicationScoped
public class AcceleratePhaseListener {

	@Inject
	private Validator validator;

	@Inject
	@Unchecked
	private Converter converter;

	@Inject
	private PageMetadataService pageMetadataService;

	@Inject
	private Identity identity;

	@Inject
	private AuditService auditService;

	public void processPhase(@Observes UiPhaseEvent event) throws Exception {
		FacesContext facesContext = event.getEvent().getFacesContext();
		PhaseId phaseId = event.getEvent().getPhaseId();

		if (facesContext.getResponseComplete()) {
			return;
		}

		UIViewRoot viewRoot = facesContext.getViewRoot();
		boolean isPostback = facesContext.isPostback();
		if (viewRoot != null && viewRoot.getViewId() != null) {
			PageMetadata pageMetadata = pageMetadataService.getPageMetadataByViewId(viewRoot.getViewId());
			if (pageMetadata != null) {
				// Checks for the view security
				Page page = pageMetadata.getPage();
				if (phaseId == PhaseId.RESTORE_VIEW && !isPostback) {
					checkPagePermission(page);
					auditService.audit(page, "page.view", null, AuditLevel.INFO);
				}
				// Injects ui params
				if (phaseId == PhaseId.RESTORE_VIEW) {
					injectParams((HttpServletRequest) facesContext.getExternalContext().getRequest(), isPostback,
							pageMetadata);
				}
				// Calls ui phase actions
				callPhaseActions(phaseId, event.isAfter(), isPostback, pageMetadata);
			}
		}
	}

	/**
	 * Checks inherited permissions.
	 */
	protected void checkPagePermission(Page page) {
        // empty view action means don't check
        if (page != null && !StrUtils.isEmpty(page.getViewAction())) {
            identity.checkOneOfPermissions(null, StringUtils.split(page.getViewAction(), '|'));
        }
    }

	private void injectParams(HttpServletRequest request, boolean isPostback, PageMetadata pageMetadata)
			throws Exception {
		for (PageControllerMetadata pageControllerMetadata : pageMetadata.getControllersWithChilds()) {
			Object controller = pageControllerMetadata.getControllerObject();
			for (Field field : pageControllerMetadata.getUiParams()) {
				UiParam uiParam = field.getAnnotation(UiParam.class);
				if (!isPostback || uiParam.onPostback()) {
					setParamFieldValue(request, pageControllerMetadata.getControllerClass(), controller, field, uiParam);
				}
			}
			for (Method method : pageControllerMetadata.getUiParamSetters()) {
				UiParam uiParam = method.getAnnotation(UiParam.class);
				if (!isPostback || uiParam.onPostback()) {
					setParamMethod(request, pageControllerMetadata.getControllerClass(), controller, method, uiParam);
				}
			}
		}
	}

	// @SuppressWarnings("unchecked")
	private void setParamMethod(HttpServletRequest request, Class<?> controllerClass, Object controller, Method method,
			UiParam uiParam) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String paramName = uiParam.value();
		if (StrUtils.isEmpty(paramName)) {
			paramName = StringUtils.uncapitalize(StringUtils.removeStart(method.getName(), "set"));
		}
		Object convertedValue = request.getParameter(paramName);
		if (convertedValue != null) {
			convertedValue = converter.toObject(convertedValue, method.getParameterTypes()[0]);
			/*
			Set<?> constraintViolations = validator.unwrap(MethodValidator.class).validateParameter(controller, method,
					convertedValue, 0);
			if (!constraintViolations.isEmpty()) {
				throw new ConstraintViolationException((Set<ConstraintViolation<?>>) constraintViolations);
			}*/
		}
		if (convertedValue != null || uiParam.nullIfMissed()) {
			method.invoke(controller, convertedValue);
		}
	}

	@SuppressWarnings("unchecked")
	private void setParamFieldValue(HttpServletRequest request, Class<?> controllerClass, Object controller,
			Field field, UiParam uiParam) throws IllegalAccessException {
		String paramName = StrUtils.defaultIfEmpty(uiParam.value(), field.getName());
		String paramValue = request.getParameter(paramName);
		Object convertedValue = converter.toObject(paramValue, field.getType());
		Set<?> constraintViolations = validator.validateValue(controllerClass, field.getName(), convertedValue);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException((Set<ConstraintViolation<?>>) constraintViolations);
		}
		if (convertedValue != null || uiParam.nullIfMissed()) {
			field.set(controller, convertedValue);
		}
	}

	private void callPhaseActions(PhaseId phaseId, boolean after, boolean isPostback, PageMetadata pageMetadata)
			throws Exception {
		for (PageControllerMetadata pageControllerMetadata : pageMetadata.getControllersWithChilds()) {
			Object controller = pageControllerMetadata.getControllerInstance();
			for (Method method : pageControllerMetadata.getUiPhaseActions()) {
				if (shouldCall(method, phaseId, isPostback, after)) {
					callPhaseActionMethod(controller, method);
				}
			}
		}
	}

	private void callPhaseActionMethod(Object controller, Method method) throws IllegalAccessException,
			InvocationTargetException {
		method.invoke(controller);
		/*ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
		String expr = "#{"
				+ StringUtils.uncapitalize(pageMetaData.getControllerClass().getSimpleName()) + "."
				+ method.getName() + "}";
		MethodExpression methodExpression = expressionFactory.createMethodExpression(
				facesContext.getELContext(), expr, void.class, new Class<?>[] {});
		methodExpression.invoke(facesContext.getELContext(), null);*/
	}

	private boolean shouldCall(Method method, PhaseId phaseId, boolean isPostback, boolean after) {
		UiPhaseAction info = method.getAnnotation(UiPhaseAction.class);
		if (info.after() != after) {
			return false;
		}
		if (info.value().equals(phaseId) || info.value() == co.pishfa.accelerate.ui.phase.PhaseId.ANY_PHASE) {
			return !isPostback || info.onPostback();
		}
		return false;
	}

}

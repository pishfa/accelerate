package co.pishfa.accelerate.portal.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.Bean;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.portal.entity.Page;
import co.pishfa.accelerate.reflection.ReflectionUtils;
import co.pishfa.accelerate.ui.controller.ViewController;
import co.pishfa.accelerate.ui.param.UiParam;
import co.pishfa.accelerate.ui.phase.UiPhaseAction;

/**
 * Processed information about a page. This is typically assembled from data in @Page annotation or Page entities in the
 * database.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class PageMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	public static class PageControllerMetadata {
		protected Class<?> controllerClass;
		// Cache it for faster access
		protected Bean<?> controllerBean;
		protected List<Method> uiPhaseActions;
		protected List<Field> uiParams;
		protected List<Method> uiParamSetters;

		public PageControllerMetadata() {
		}

		public PageControllerMetadata(Class<?> controllerClass) {
			this.controllerClass = controllerClass;
			this.controllerBean = CdiUtils.getExactBean(CdiUtils.getBeanManager(), controllerClass);
			extractActions(controllerClass);
			extractParams(controllerClass);
		}

		protected void extractParams(Class<?> controllerClasse) {
			this.uiParams = ReflectionUtils.getAllFieldsAnnotatedWith(controllerClasse, UiParam.class);
			for (Field field : uiParams) {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
			}
			this.uiParamSetters = ReflectionUtils.getAllMethodsAnnotatedWith(controllerClasse, UiParam.class);
			for (Method method : uiParamSetters) {
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
			}
		}

		protected void extractActions(Class<?> controllerClasse) {
			this.uiPhaseActions = ReflectionUtils.getAllMethodsAnnotatedWith(controllerClasse, UiPhaseAction.class);
			// TODO is it right?
			for (Method method : uiPhaseActions) {
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
			}
		}

		public Class<?> getControllerClass() {
			return controllerClass;
		}

		public List<Method> getUiPhaseActions() {
			return uiPhaseActions;
		}

		public Bean<?> getControllerBean() {
			return controllerBean;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Object getControllerInstance() {
			return CdiUtils.getBeanReference(CdiUtils.getBeanManager(), (Bean) getControllerBean(),
					getControllerClass());
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Object getControllerObject() {
			return CdiUtils.getBeanObject(CdiUtils.getBeanManager(), (Bean) getControllerBean(), getControllerClass());
		}

		public List<Field> getUiParams() {
			return uiParams;
		}

		public List<Method> getUiParamSetters() {
			return uiParamSetters;
		}

	}

	public static class PageChildControllerMetadata extends PageControllerMetadata {
		private final Object controllerObj;

		public PageChildControllerMetadata(Object controllerObj) {
			this.controllerObj = controllerObj;
			this.controllerClass = controllerObj.getClass();
			extractActions(controllerClass);
			extractParams(controllerClass);
		}

		@Override
		public Object getControllerInstance() {
			return controllerObj;
		}

		@Override
		public Object getControllerObject() {
			return controllerObj;
		}

	}

	private final List<PageControllerMetadata> controllers = new ArrayList<>(1);
	private PageControllerMetadata primaryController;
	private Page page;

	public List<PageControllerMetadata> getControllers() {
		return controllers;
	}

	public List<PageControllerMetadata> getControllersWithChilds() {
		List<PageControllerMetadata> res = new ArrayList<>();
		for (PageControllerMetadata controller : controllers) {
			res.add(controller);
			// find child controllers and add them too
			Object controllerObject = controller.getControllerObject();
			addChildControllers(res, controllerObject);
		}
		return res;
	}

	private void addChildControllers(List<PageControllerMetadata> res, Object controllerObject) {
		if (controllerObject != null && controllerObject instanceof ViewController) {
			List<ViewController> childControllers = ((ViewController) controllerObject).getChildControllers();
			if (childControllers != null)
				for (Object childController : childControllers) {
					res.add(new PageChildControllerMetadata(childController));
					addChildControllers(res, childController);
				}
		}
	}

	public PageControllerMetadata getPrimaryController() {
		return primaryController;
	}

	public void setPrimaryController(PageControllerMetadata primaryController) {
		this.primaryController = primaryController;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

}

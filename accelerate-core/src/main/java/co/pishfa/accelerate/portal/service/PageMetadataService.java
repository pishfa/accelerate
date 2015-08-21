package co.pishfa.accelerate.portal.service;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.core.FrameworkExtension;
import co.pishfa.accelerate.core.FrameworkStartedEvent;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.initializer.api.InitializerFactory;
import co.pishfa.accelerate.initializer.core.BaseInitListener;
import co.pishfa.accelerate.initializer.model.InitEntityMetadata;
import co.pishfa.accelerate.portal.service.PageMetadata.PageControllerMetadata;
import co.pishfa.accelerate.portal.entity.Page;
import co.pishfa.accelerate.resource.ResourceUtils;
import co.pishfa.accelerate.ui.UiExtention;
import co.pishfa.accelerate.ui.UiUtils;
import co.pishfa.accelerate.ui.controller.UiController;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.service.AuthorizationService;
import co.pishfa.security.entity.authorization.Action;
import org.apache.commons.lang3.StringUtils;
import org.omnifaces.cdi.ViewScoped;
import org.slf4j.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Service
@Named
public class PageMetadataService {

	public static final String GLOBAL = "*";

	@Inject
	private Logger logger;

    @Inject
    private AuthorizationService authorizationService;

	public static PageMetadataService getInstance() {
		return CdiUtils.getInstance(PageMetadataService.class);
	}

	private final Map<String, PageMetadata> viewIdToPage = new Hashtable<>();
	private final Map<String, PageMetadata> nameToPage = new Hashtable<>();

	private class PagesListener extends BaseInitListener {
		@Override
		public void entityCreated(InitEntityMetadata initEntity, Object entityObj) {
			if (entityObj instanceof Page) {
				Page page = (Page) entityObj;
				// If title is not explicitly defined, try to guess it from title of its original view action
				if (StrUtils.isEmpty(page.getTitle()) && !StrUtils.isEmpty(page.getViewAction())) {
					Action action = authorizationService.findAction(page.getViewAction());
                    if(action != null)
					    page.setTitle(action.getTitle());
				}
                //view action inheritance
                if(page.getViewAction() == null && page.getParent() != null) {
                    page.setViewAction(page.getParent().getViewAction());
                }

				PageMetadata metaData = new PageMetadata();
				metaData.setPage(page);
				if (page.getViewId() != null) {
					if (viewIdToPage.containsKey(page.getViewId())) {
						logger.warn("Multiple pages with viewId {} found in pages.xml", page.getViewId());
					}
					viewIdToPage.put(page.getViewId(), metaData);
					// auto create name from /foo/bar.xhtml -> bar
					if (page.getName() == null) {
						String id = page.getViewId();
						int begin = id.lastIndexOf('/') + 1;
						int end = id.lastIndexOf(".xhtml");
						if (end == -1)
							end = id.length();
						page.setName(id.substring(begin, end));
					}
				}
				if (page.getName() != null) {
					// check for relative names
					if (page.getName().startsWith(":") && page.getParent() != null) {
						page.setName(page.getParent().getName() + page.getName());
					}
					if (nameToPage.containsKey(page.getName())) {
						logger.warn("Multiple pages with the name {} found in pages.xml", page.getName());
					}
					nameToPage.put(page.getName(), metaData);
				}
			}
		}
	}

	public void onStart(@Observes FrameworkStartedEvent event) throws Exception {
		// Load pages.xml:
		new InitializerFactory().entityClasses(FrameworkExtension.getAnnotatedEntities()).incremental(false)
				.autoAnchor(true).key("name").create(new PagesListener())
				.read(ResourceUtils.getResourceAsStream("pages.xml"), true);

		List<Class<?>> controllers = UiExtention.getControllers();
		List<Class<?>> globalControllers = new ArrayList<>();
		for (Class<?> controllerClass : controllers) {
			UiController info = controllerClass.getAnnotation(UiController.class);
			String outcome = info.value();
			// auto name controller
			if (StrUtils.isEmpty(outcome)) {
				outcome = StringUtils.uncapitalize(controllerClass.getSimpleName());
				// if no such page exists, ignore it
				if (getPageMetadataByName(outcome) == null)
					outcome = null;
				else
					outcome = "ac:" + outcome;
			}
			if (GLOBAL.equals(outcome)) {
				globalControllers.add(controllerClass);
			} else if (!StrUtils.isEmpty(outcome)) {
				PageMetadata pageMetadata = getPageMetadata(outcome);
				// Even if this viewId is not listed in pages.xml, create one for it
				if (pageMetadata == null) {
					pageMetadata = new PageMetadata();
					Page page = new Page();
					page.setViewId(outcome);
					pageMetadata.setPage(page);
					viewIdToPage.put(outcome, pageMetadata);
				}
				PageControllerMetadata pageControllerMetadata = new PageControllerMetadata(controllerClass);
				pageMetadata.getControllers().add(pageControllerMetadata);
				if (info.primary()) {
					if (pageMetadata.getPrimaryController() != null) {
						logger.warn("Multiple primary controllers found for viewId " + outcome);
					} else {
						pageMetadata.setPrimaryController(pageControllerMetadata);
					}
				}
			}
		}
		// Add global controllers to all current metadata
		for (Class<?> controllerClass : globalControllers) {
			PageControllerMetadata pageControllerMetadata = new PageControllerMetadata(controllerClass);
			for (PageMetadata pageMetadata : viewIdToPage.values()) {
				pageMetadata.getControllers().add(pageControllerMetadata);
			}
		}
	}

	public PageMetadata getPageMetadataByViewId(String viewId) {
		return viewIdToPage.get(viewId);
	}

	public PageMetadata getPageMetadataByName(String name) {
		return nameToPage.get(name);
	}

	public PageMetadata getPageMetadata(String outcome) {
		if (outcome.startsWith("ac:")) {
			return getPageMetadataByName(outcome.substring("ac:".length()));
		} else
			return getPageMetadataByViewId(outcome);
	}

	@Produces
	@ViewScoped
	@Named("page")
	public Page getPage() {
		return getPageMetadata().getPage();
	}

	/*@Produces
	@Named("controller")
	@RequestScoped*/
	public Object getController() {
		PageControllerMetadata primaryController = getPageMetadata().getPrimaryController();
		if (primaryController == null) {
			throw new IllegalStateException("No controller defined for viewId " + UiUtils.getViewId());
		}
		return primaryController.getControllerObject();
	}

	@Produces
	@ViewScoped
	public PageMetadata getPageMetadata() {
		String viewId = UiUtils.getViewId();
		if (viewId != null) {
			PageMetadata pageMetadata = getPageMetadataByViewId(viewId);
			if (pageMetadata != null) {
				return pageMetadata;
			} else {
				throw new IllegalStateException("ViewId " + viewId + " has no page metadata");
			}
		}
		throw new IllegalStateException("No view root to retrieve the view id from");
	}

	public PageMetadata getRootPageMetadata() {
		return getPageMetadataByName("home");
	}

}

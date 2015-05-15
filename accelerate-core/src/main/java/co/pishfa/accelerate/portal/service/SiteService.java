/**
 * 
 */
package co.pishfa.accelerate.portal.service;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;

import co.pishfa.accelerate.cdi.Current;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.portal.entity.Site;
import co.pishfa.accelerate.ui.UiUtils;

/**
 * @author Taha Ghasemi
 * 
 */
@Service
public class SiteService {

	/**
	 * The request parameter name under which the current site is stored.
	 */
	public static final String SITE_REQ_PARAM_NAME = "site.name";

	private final Map<String, Site> sitesByName = new HashMap<>();

	@Inject
	private Event<SiteRegisteredEvent> siteRegisteredEvent;

	@Produces
	@Current
	@RequestScoped
	public Site getCurrentSite() {
		Site site = null;
		try {
			String siteName = UiUtils.getRequest().getParameter(SITE_REQ_PARAM_NAME);
			site = sitesByName.get(siteName);
		} catch (Exception e) {
			// log.warn("Could not access the current request.", e);
		}
		return site;
	}

	public void registerSite(final Site site) {
		Validate.isTrue(!sitesByName.containsKey(site.getName()), "Site %s is already registered", site.getName());

		sitesByName.put(site.getName(), site);
		siteRegisteredEvent.fire(new SiteRegisteredEvent(site));
	}

}

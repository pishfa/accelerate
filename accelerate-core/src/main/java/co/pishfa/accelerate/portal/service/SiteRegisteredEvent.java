/**
 * 
 */
package co.pishfa.accelerate.portal.service;

import co.pishfa.accelerate.portal.entity.Site;

/**
 * @author Taha Ghasemi
 * 
 */
public class SiteRegisteredEvent {

	private final Site site;

	public Site getSite() {
		return site;
	}

	/**
	 * @param site
	 */
	public SiteRegisteredEvent(Site site) {
		super();
		this.site = site;
	}

}

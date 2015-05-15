/**
 * 
 */
package co.pishfa.accelerate.ui.navigation;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.message.UserMessages;
import co.pishfa.accelerate.portal.service.PageMetadata;
import co.pishfa.accelerate.portal.service.PageMetadataService;
import co.pishfa.accelerate.portal.entity.Page;
import co.pishfa.accelerate.ui.UiUtils;
import co.pishfa.accelerate.ui.controller.GlobalController;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.accelerate.utility.UriUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Taha Ghasemi
 */
@ApplicationScoped
public class AccelerateNavigationHandler implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String RETURN_VIEW = "return-view";
	private static final String URL_QE = "%3F"; // ?
	private static final String[] ENCODE = { "=", "&" };
	private static String[] ENCODE_REPLACE = null;
	static {
		ENCODE_REPLACE = new String[ENCODE.length];
		for (int i = 0; i < ENCODE.length; i++) {
			ENCODE_REPLACE[i] = '%' + Integer.toHexString(ENCODE[i].charAt(0)).toUpperCase();
		}
	}
	public static final String PREFIX = "ac:";

	public static AccelerateNavigationHandler getInstance() {
		return CdiUtils.getInstance(AccelerateNavigationHandler.class);
	}

	@Inject
	private PageMetadataService pageMetadataService;

	@Inject
	private UserMessages userMessages;

	@Inject
	private GlobalController pageController;

	@Inject
	private Page page;

	@Inject
	private Logger log;

	public AccelerateNavigationHandler() {
	}

	private String translate(Page page, String dest) {
		// Strip out the parameters section, and put the base view id into to
		int paramStart = dest.indexOf('?');
		String to = paramStart < 0 ? dest.substring(PREFIX.length()) : dest.substring(PREFIX.length(), paramStart);

		StringBuilder toParams = new StringBuilder();
		if ("this".equals(to)) {
			to = page.getViewId();
		} else if ("parent".equals(to)) {
			to = navigateToParent(page, toParams);
		} else {
			to = navigateToPageWithName(page, to, toParams);
		}

		if (StrUtils.isEmpty(to)) {
			log.warn("Could not determine the target page for " + dest);
			return null;
		}
		StringBuilder finalTo = new StringBuilder(to);

		// The default is to redirect unless specified
		if (!dest.contains("faces-redirect")) {
			if (paramStart < 0) {
				finalTo.append("?faces-redirect=true");
			} else {
				finalTo.append(dest.substring(paramStart)).append("&faces-redirect=true");
			}
		} else {
			finalTo.append(dest.substring(paramStart));
		}

		finalTo.append(toParams);
		return finalTo.toString();
	}

	public String navigateToPageWithName(Page page, String to, StringBuilder toParams) {
		// resolve relative names
		if (to.startsWith(":")) {
			to = page.getName() + to;
		}
		Page destPage = null;
		PageMetadata pageMetadata = pageMetadataService.getPageMetadataByName(to);
		if (pageMetadata != null) {
			destPage = pageMetadata.getPage();
		}

		if (destPage == null) {
			throw new IllegalArgumentException("No page with name " + to + " is defined.");
		}

		if (destPage.isDynamic()) {
			// The destPage is dynamic, so pass the information of current current as its parent
			String viewId = UiUtils.getViewId();
			if (viewId != null && !viewId.equals(destPage.getViewId())) {
				toParams.append("&").append(RETURN_VIEW).append('=').append(viewId); // jsf encodes it
				String query = UiUtils.getRequest().getQueryString();
				if (query != null) {
					// we encode it again since jsf complains about = and & (but it shouldn't)
					toParams.append(URL_QE).append(encodePath(query));
				}
			}
		}

		return destPage.getViewId();
	}

	private String encodePath(String query) {
		return StringUtils.replaceEach(query, ENCODE, ENCODE_REPLACE);
	}

	public String navigateToParent(Page page, StringBuilder toParams) {
		String to = null;
		if (page != null) {
			// If current is dynamic, determine parent from returnView param
			String returnView = pageController.getReturnView();
			if (page.isDynamic() && returnView != null) {
				int returnViewParamStart = returnView.indexOf(URL_QE);
				if (returnViewParamStart < 0) {
					to = returnView;
				} else {
					to = returnView.substring(0, returnViewParamStart);
					// decode due to additional encoding above
					toParams.append("&").append(
							UriUtils.decodeURL(returnView.substring(returnViewParamStart + URL_QE.length())));
				}
			} else {
                to = findParent(page);
            }
		} else {
			throw new RuntimeException("parent can only be defined when current is not null.");
		}
		return to;
	}

    private String findParent(Page page) {
        Page parent = page.getParent();
        while (parent != null && parent.getViewId() == null) {
            parent = parent.getParent();
        }

        if (parent != null && !parent.isDynamic()) {
            return parent.getViewId();
        } else {
            throw new RuntimeException("Could not determine parent for current " + page);
        }
    }

    public String translate(String outcome) {
		if (canHandle(outcome)) {
			outcome = translate(page, outcome);
		}
		return outcome;
	}

	public boolean canHandle(String outcome) {
		return outcome != null && outcome.startsWith(PREFIX);
	}
}

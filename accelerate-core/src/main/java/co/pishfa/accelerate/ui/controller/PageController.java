package co.pishfa.accelerate.ui.controller;

import co.pishfa.accelerate.cache.UiCached;
import co.pishfa.accelerate.i18n.domain.Locale;
import co.pishfa.accelerate.portal.entity.Page;
import co.pishfa.accelerate.portal.service.PageMetadata;
import co.pishfa.accelerate.portal.service.PageMetadataService;
import co.pishfa.security.entity.authentication.Identity;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * A controller for a whole page. It provides title, menu, and bread crumb of the page.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class PageController extends ViewController {

	private static final long serialVersionUID = 1L;

	@Inject
	private PageMetadata pageMetadata;

	@Inject
	private Identity identity;

    @Inject
    private Locale locale;

    @Inject
    private MenuBuilder menuBuilder;

	public String getPageTitle() {
		return getPage() != null ? getPage().getTitle() : null;
	}

	public PageMetadata getPageMetadata() {
		return pageMetadata;
	}

	public Page getPage() {
		return pageMetadata.getPage();
	}

	public Page getRootPage() {
		return PageMetadataService.getInstance().getRootPageMetadata().getPage();
	}

	@UiCached
	public List<Page> getBreadCrumbItems() {
		List<Page> history = new ArrayList<>();
		if (getPage() != null) {
			Page root = getRootPage();
			Page prev = getPage().getParent();
			while (prev != null && !root.equals(prev)) {
				history.add(0, prev);
				prev = prev.getParent();
			}
		}
		return history;
	}

    public Object getMenuModel(Page root) {
        return getMenuBuilder().getMenu(root);
    }

    public Object getMenuModel() {
        return getMenuModel(getRootPage());
    }

    public List<Page> getMenuItems(Page root) {
        if (root == null) {
            return null;
        }
        List<Page> res = new ArrayList<>();
        for (Page page : root.getChildren()) {
            if (page.isVisible()) {
                res.add(page);
            }
        }
        return res;
    }

	public Identity getIdentity() {
		if (identity != null)
			return identity;
		return Identity.getInstance();
	}

    public Locale getLocale() {
        if (locale != null)
            return locale;
        return Locale.getInstance();
    }

    protected MenuBuilder getMenuBuilder() {
        return menuBuilder;
    }

}

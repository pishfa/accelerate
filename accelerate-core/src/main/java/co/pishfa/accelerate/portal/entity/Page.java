/**
 * 
 */
package co.pishfa.accelerate.portal.entity;

import javax.persistence.*;

import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.accelerate.entity.hierarchical.BaseHierarchicalEntity;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.entity.authentication.Identity;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Entity
@Cacheable
@InitEntity(key = "-")
@Table(name = "ac_page")
public class Page extends BaseHierarchicalEntity<Page> {

	private static final long serialVersionUID = 1L;

	private String title;

	private String viewAction;

	@InitProperty(dynamic = false)
	private String url;

	@InitProperty(dynamic = false)
	private String action;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, optional = true)
	private Site site;

	private String viewId;

	private boolean dynamic = false;

	private String image;

	private String icon;

    private String help;

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getViewAction() {
		return viewAction;
	}

	public void setViewAction(String action) {
		this.viewAction = action;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getOutcome() {
		if (!StrUtils.isEmpty(viewId)) {
			if (StrUtils.isEmpty(getName()))
				return viewId;
			else
				return "ac:" + getName();
		} else
			return null;
	}

    public boolean isVisible() {
        return !isDynamic() && !StrUtils.isEmpty(getTitle()) && (StrUtils.isEmpty(getViewAction()) || Identity.getInstance().canAny(getViewAction()));
    }

    public boolean hasAnyVisibleChild() {
        for(Page child : getChildren()) {
            if (child.isVisible()) {
                return true;
            }
        }
        return false;
    }

}

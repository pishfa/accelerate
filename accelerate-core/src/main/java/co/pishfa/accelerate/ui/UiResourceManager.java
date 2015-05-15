package co.pishfa.accelerate.ui;

import co.pishfa.accelerate.core.ConfigAppliedEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@ApplicationScoped
@Named("res")
public class UiResourceManager {

    private String base;

    public void onConfiguration(@Observes final ConfigAppliedEvent event) {
        base = event.getConfig().getString("ui.baseResource");
    }

    public String lib() { return "default"; }

	public String base() {
		return base == null? UiUtils.getRequest().getContextPath() + "/resources/default/" : base;
	}

	public String img(String name) {
		return base() + "img/" + name;
	}

	public String css(String name) {
		return base() + "css/" + name;
	}

	public String js(String name) {
		return base() + "js/" + name;
	}

}

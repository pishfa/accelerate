package co.pishfa.accelerate.ui;

import java.util.Collection;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.accelerate.utility.UriUtils;

/**
 * Some utility functions to be used in view pages.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@ApplicationScoped
@Named("ui")
public class UiFunctions {

	public String sanitizeUri(final String uri) {
		return UriUtils.sanitizeUri(uri);
	}

	public int length(final String string) {
		return string == null ? 0 : string.length();
	}

	public String repeatString(final String s, final Integer count) {
		StringBuilder spaces = new StringBuilder();
		for (int i = 0; i < count; i++) {
			spaces.append(s);
		}
		return spaces.toString();
	}

	// Some null-safe operations
	public int sizeOf(final Collection<?> col) {
		return col == null ? 0 : col.size();
	}

	public String toString(final int i) {
		return i + "";
	}

	public String concat(final String a, final String b) {
		return a + b;
	}

	public boolean isEmpty(final String str) {
		return StrUtils.isEmpty(str);
	}

	public int abs(int a) {
		return Math.abs(a);
	}

	public String getClientId(final String id) {
		UIComponent component = UiUtils.getViewRoot().findComponent(id);
		return component == null ? null : component.getClientId(FacesContext.getCurrentInstance());
	}

	public String getCurrentURL() {
		return UriUtils.getCurrentUrl();
	}

	public boolean isPageHasError() {
		Severity maximumSeverity = FacesContext.getCurrentInstance().getMaximumSeverity();
		return maximumSeverity != null && maximumSeverity.getOrdinal() != 0;
	}

	public Date now() {
		return new Date();
	}

}

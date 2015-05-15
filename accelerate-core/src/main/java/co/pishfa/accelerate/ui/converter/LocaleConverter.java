package co.pishfa.accelerate.ui.converter;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import co.pishfa.accelerate.i18n.domain.Locale;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@ApplicationScoped
@Named
public class LocaleConverter implements javax.faces.convert.Converter {

	@Inject
	private Locale locale;

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent cmp, String value) {
		return locale.convert(value);
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent cmp, Object value) {
		return String.valueOf(value);
	}

}

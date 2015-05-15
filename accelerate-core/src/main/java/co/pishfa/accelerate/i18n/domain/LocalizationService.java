/**
 * 
 */
package co.pishfa.accelerate.i18n.domain;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.config.Config;
import co.pishfa.accelerate.core.ConfigAppliedEvent;
import co.pishfa.accelerate.message.Messages;
import co.pishfa.accelerate.service.Service;

import javax.enterprise.event.Observes;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.Locale;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Service
public class LocalizationService implements Serializable {

	public static LocalizationService getInstance() {
		return CdiUtils.getInstance(LocalizationService.class);
	}

	private Map<Locale, Messages> messagesPerLocale;
	private Map<Locale, Localizer> localizersPerLocale;

	public void onEvent(@Observes ConfigAppliedEvent event) {
		Config config = event.getConfig();
		Locale.setDefault(new Locale(config.getString("i18n.defaultLocale")));

		List<String> languages = config.getList("i18n.supportedLocales", String.class);

		messagesPerLocale = new HashMap<Locale, Messages>(languages.size() * 2 + 1);
		for (String language : languages) {
			Locale locale = new Locale(language);
			messagesPerLocale.put(locale, new Messages(ResourceBundle.getBundle("messages", locale)));
		}

		List<Localizer> localizers = CdiUtils.getAllInstances(Localizer.class);
		localizersPerLocale = new HashMap<>(localizers.size() * 2 + 1);
		for (Localizer localizer : localizers) {
			localizersPerLocale.put(localizer.getLocale(), localizer);
		}
	}

	public Messages getMessages(@NotNull Locale locale) {
		return messagesPerLocale.get(locale);
	}

	public Localizer getLocalizer(@NotNull Locale locale) {
		Localizer localizer = localizersPerLocale.get(locale);
		if (localizer == null) {
			localizer = new DefaultLocalizer(locale);
			localizersPerLocale.put(locale, localizer);
		}
		return localizer;
	}

}

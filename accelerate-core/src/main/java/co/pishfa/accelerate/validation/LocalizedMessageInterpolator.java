package co.pishfa.accelerate.validation;

import static javax.validation.Validation.byDefaultProvider;

import javax.validation.MessageInterpolator;

import co.pishfa.accelerate.i18n.domain.Locale;
import co.pishfa.accelerate.i18n.domain.LocalizationService;
import co.pishfa.accelerate.message.Messages;

/**
 * An interpolator that uses the {@link Locale} for resolving messages and current locale.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class LocalizedMessageInterpolator implements MessageInterpolator {

	private final MessageInterpolator delegate;

	public LocalizedMessageInterpolator() {
		this.delegate = byDefaultProvider().configure().getDefaultMessageInterpolator();
	}

	@Override
	public String interpolate(String message, MessageInterpolator.Context context) {
		return delegate.interpolate(message, context, Locale.getInstance().getLocale());
	}

	@Override
	public String interpolate(String message, MessageInterpolator.Context context, java.util.Locale locale) {
		Messages msg = LocalizationService.getInstance().getMessages(locale);
		if (msg.containsKey(message))
			return msg.get(message);
		return delegate.interpolate(message, context, locale);
	}

}

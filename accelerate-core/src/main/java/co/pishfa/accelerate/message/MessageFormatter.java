package co.pishfa.accelerate.message;

import java.text.MessageFormat;

/**
 * Combines a string with place holders for parameters and a set of parameters to produce a final string.
 */
public class MessageFormatter {

	/**
	 * Formats the given template with the given parameters.
	 */
	public String format(final String template, final Object... params) {
		return MessageFormat.format(template, params);
	}
}

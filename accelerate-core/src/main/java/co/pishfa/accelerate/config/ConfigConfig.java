/**
 * 
 */
package co.pishfa.accelerate.config;

import co.pishfa.accelerate.convert.Converter;
import co.pishfa.accelerate.convert.DefaultConverter;

/**
 * @author Taha Ghasemi
 * 
 */
public class ConfigConfig {

	private static ConfigConfig instance;

	public synchronized static ConfigConfig getInstance() {
		if (instance == null)
			instance = new ConfigConfig();
		return instance;
	}

	private final Converter converter = new DefaultConverter();
	private final char separatorChar = ',';

	/**
	 * @return the resolver
	 */
	public Converter getConverter() {
		return converter;
	}

	/**
	 * @return the separatorChar
	 */
	public char getSeparatorChar() {
		return separatorChar;
	}

}

package co.pishfa.accelerate.initializer.util;

import java.io.InputStream;

import org.apache.commons.lang3.Validate;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class Input {

	public static InputStream resource(String name) {
		Validate.notNull(name, "resource name should not be null");

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		Validate.notNull(is, "No resource with name " + name + " is found.");
		return is;
	}

}

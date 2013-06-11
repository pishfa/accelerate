package co.pishfa.accelerate.initializer.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.Validate;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class Input {

	public static Reader resource(String name) throws UnsupportedEncodingException {
		Validate.notNull(name, "resource name should not be null");

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		Validate.notNull(is, "No resource with name " + name + " is found.");
		return new InputStreamReader(is, "utf-8");
	}

}

package co.pishfa.accelerate.initializer.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class Input {

	public static Reader resource(String name) throws UnsupportedEncodingException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		return is == null ? null : new InputStreamReader(is, "utf-8");
	}

}

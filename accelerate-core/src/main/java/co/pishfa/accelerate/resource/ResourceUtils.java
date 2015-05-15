/**
 * 
 */
package co.pishfa.accelerate.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Taha Ghasemi
 * 
 */
public class ResourceUtils {

	/**
	 * Don't forget to close it after use!
	 * 
	 * @param name
	 * @return
	 */
	public static InputStream getResourceAsStream(String name) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	}

	public static URL getResource(String name) {
		return Thread.currentThread().getContextClassLoader().getResource(name);
	}

	public static List<URL> getResources(String name) throws IOException {
		List<URL> result = null;
		Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(name);
		result = new ArrayList<>();
		while (resources.hasMoreElements()) {
			result.add(resources.nextElement());
		}
		return result;
	}

}

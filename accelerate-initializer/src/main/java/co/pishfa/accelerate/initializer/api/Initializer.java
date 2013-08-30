package co.pishfa.accelerate.initializer.api;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;

/**
 * Builds an object graph from an xml file. The main purpose is to initialize the database from an xml file. The
 * initializer can be created once and then be used for reading multiple xml files (the anchors are shared). This class
 * is not thread-safe.
 * 
 * @author Taha Ghasemi
 * 
 */
public interface Initializer {

	/**
	 * Reads data from specified resource.
	 * 
	 * @return a map from first level element names to the list of defined entities with that name
	 */
	Map<String, List<Object>> read(String resourceName) throws Exception;

	/**
	 * Reads data from the given inputStream. The encoding is detected from xml declaration. Note if autoClose is false,
	 * you are responsible for closing this reader.
	 * 
	 * @return a map from first level element names to the list of defined entities with that name
	 */
	Map<String, List<Object>> read(InputStream input, boolean autoClose) throws Exception;

	/**
	 * Reads data from the given xml element.
	 * 
	 * @return a map from first level element names to the list of defined entities with that name
	 */
	Map<String, List<Object>> read(Element root) throws Exception;

	/**
	 * Reads data from a set of annotations defined in the provided class.
	 * 
	 * @return
	 */
	Map<String, List<Object>> read(Class<?> data);

	/**
	 * 
	 * @return all anchores by their name
	 */
	Map<String, Object> getAnchores();

	/**
	 * Puts the given entity with specific anchor name into the anchors. If the anchorName starts with ":", the alias of
	 * entity class will be appended at the beginning of the anchor name
	 * 
	 * @return the previous object with the same anchorName, if any.
	 */
	Object putObject(String anchorName, Object entity);

	/**
	 * @param anchorName
	 *            the entity anchor name. Can be relative name such as 'name' which resolves based on the provided
	 *            entityClass.
	 * @return the entity with provided anchorName. Null if no such entity exists.
	 */
	<T> T getObject(String anchorName, Class<T> entityClass);

	/**
	 * @return the entity which corresponds to the given data class. Null if no such entity exists.
	 */
	<T> T getObject(Class<?> dataClass, Class<T> entityClass);

}
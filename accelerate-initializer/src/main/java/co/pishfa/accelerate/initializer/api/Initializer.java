package co.pishfa.accelerate.initializer.api;

import java.io.Reader;
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
	 * @return
	 */
	Map<String, List<Object>> read(String resourceName) throws Exception;

	/**
	 * Reads data from the given reader. Note if autoClose is false, you are responsible for closing this reader.
	 * 
	 * @return
	 */
	Map<String, List<Object>> read(Reader reader, boolean autoClose) throws Exception;

	Map<String, List<Object>> read(Element root) throws Exception;

	/**
	 * 
	 * @return all anchores
	 */
	Map<String, Object> getAnchores();

	/**
	 * Puts the given entity with specific anchor name into the anchors
	 * 
	 * @return the previous object with the same anchorName, if any.
	 */
	Object putObject(String anchorName, Object entity);

	/**
	 * @param anchorName
	 *            the entity anchor name. Can be relative name such as '@name' which resolves based on the provided
	 *            entityClass.
	 * @return the entity with provided anchorName. Null if no such entity exists.
	 */
	<T> T getObject(String anchorName, Class<T> entityClass);

}
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

	Map<String, List<Object>> read(String resourceName) throws Exception;

	Map<String, List<Object>> read(Reader reader) throws Exception;

	Map<String, List<Object>> read(Element root) throws Exception;

	Map<String, Object> getAnchores();

}
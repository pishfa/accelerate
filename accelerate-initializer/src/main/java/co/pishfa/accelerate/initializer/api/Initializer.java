package co.pishfa.accelerate.initializer.api;

import java.io.File;
import java.io.InputStream;

import org.jdom2.Element;

/**
 * Builds an object graph from an xml file. The main purpose is to initialize the database from an xml file. The
 * initializer can be configured once and then be used for reading multiple xml files (the anchors are shared). This
 * class is not thread-safe.
 * 
 * @author Taha Ghasemi
 * 
 */
public interface Initializer {

	Object read(File file) throws Exception;

	Object read(InputStream in) throws Exception;

	Object read(Element dataElem) throws Exception;

}
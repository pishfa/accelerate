/**
 * 
 */
package co.pishfa.accelerate.config;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Stack;

/**
 * Thread-unsafe reader of XML configuration file. This class can
 * be used to read multiple files. The changes to the configuration dose not apply to the underlying XML file.
 *
 * @author Taha Ghasemi
 * 
 */
public class XmlConfigReader  {

	private static final Logger log = LoggerFactory.getLogger(XmlConfigReader.class);

	private Stack<String> stack;
    private Config delegate;

    public XmlConfigReader(Config delegate) {
        this.delegate = delegate;
    }

    public void load(File file) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Element root = builder.build(file).getRootElement();
		load(root);
	}

	public void load(InputStream in) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Element root = builder.build(in).getRootElement();
		load(root);
	}

	public void load(Element rootElement) throws Exception {
		stack = new Stack<>();
		for (Element childElement : rootElement.getChildren()) {
			if ("properties".equals(childElement.getName())) {
				for (Element propertyElement : childElement.getChildren()) {
					processPropertyElement(propertyElement);
				}
			} else {
				processElement(childElement);
			}
		}
	}

	private void processElement(Element element) {
		String elementName = element.getName();
		String namespace;
		if (stack.isEmpty()) {
			namespace = elementName;
		} else {
			namespace = stack.peek() + "." + elementName;
		}
		stack.push(namespace);

		processNonEntityElement(element);

		stack.pop();
	}

	private void processNonEntityElement(Element element) {
		processNonEntityAttributes(element);
		for (Element child : element.getChildren()) {
			processElement(child);
		}
	}

	private void processNonEntityAttributes(Element element) {
		String namespace = stack.peek();
		for (Attribute attribute : element.getAttributes()) {
			delegate.setObject(namespace + "." + attribute.getName(), attribute.getValue());
		}
	}

	private void processPropertyElement(Element propertyElement) {
		String name = propertyElement.getAttributeValue("name");
		String value = propertyElement.getAttributeValue("value");
		delegate.setObject(name, value);
	}

}

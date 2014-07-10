package co.pishfa.accelerate.initializer.core;

import co.pishfa.accelerate.initializer.api.InitializerFactory;
import co.pishfa.accelerate.initializer.model.InitEntityMetadata;
import co.pishfa.accelerate.initializer.model.InitPropertyMetadata;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

/**
 * Reads {@link co.pishfa.accelerate.initializer.model.InitEntityMetadata} from an XML config file.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class XmlMetadataReader {

	private static final String INITIALIZER_METADATA_XSD = "/initializer-metadata.xsd";
	protected static final Namespace PISHFA_NS = Namespace.getNamespace("http://pishfa.co");

	private static final Logger log = LoggerFactory.getLogger(XmlMetadataReader.class);

	protected InitializerFactory factory;

	public XmlMetadataReader(InitializerFactory factory) {
		this.factory = factory;
	}

	public void processMetadata(InputStream input) throws Exception {
		Element root = getRootElement(input);
		for (Element entityElement : root.getChildren("entity", PISHFA_NS)) {
			InitEntityMetadata initEntity = processEntityElement(entityElement);
			factory.addInitEntity(initEntity);
		}
	}

	private Element getRootElement(InputStream configFile) throws Exception {
		File xsd = new File(getClass().getResource(INITIALIZER_METADATA_XSD).toURI());
		XMLReaderXSDFactory xsdFactory = new XMLReaderXSDFactory(xsd);
		SAXBuilder builder = new SAXBuilder(xsdFactory);
		Element root = builder.build(configFile).getRootElement();
		return root;
	}

	protected InitEntityMetadata processEntityElement(Element entityElement) throws ClassNotFoundException {
		String entityClazz = entityElement.getAttributeValue("class");
		String entityAlias = entityElement.getAttributeValue("alias");
		String entityKey = entityElement.getAttributeValue("key");
		InitEntityMetadata initEntity = new InitEntityMetadata(entityAlias, Class.forName(entityClazz), entityKey);
		String inherits = entityElement.getAttributeValue("inherits");
		if (!StringUtils.isEmpty(inherits)) {
			for (String inherit : inherits.split(",")) {
				InitEntityMetadata inheritEntity = factory.getInitEntityByAlias(inherit);
				if (inheritEntity != null) {
					for (InitPropertyMetadata property : inheritEntity.getProperties()) {
						initEntity.addProperty(property);
					}
				} else {
					log.error("Invalid alias " + inherit + " defined in inherits of entity " + initEntity.getAlias());
				}
			}
		}
		processEntityElementProperties(entityElement, initEntity);
		return initEntity;
	}

	protected void processEntityElementProperties(Element entityElement, InitEntityMetadata initEntity) {
		// read properties of an entity
		for (Element propertyElement : entityElement.getChildren("property", PISHFA_NS)) {
			InitPropertyMetadata initProperty = processPropertyElement(propertyElement);
			initEntity.addProperty(initProperty);
		}
	}

	protected InitPropertyMetadata processPropertyElement(Element propertyElement) {
		String propName = propertyElement.getAttributeValue("name");
		String propAlias = propertyElement.getAttributeValue("alias");
		String propDefault = propertyElement.getAttributeValue("default");
		String propDynamic = propertyElement.getAttributeValue("dynamic");
		InitPropertyMetadata initProperty = new InitPropertyMetadata(propName, propAlias, propDefault,
				!"false".equals(propDynamic));
		return initProperty;
	}

}

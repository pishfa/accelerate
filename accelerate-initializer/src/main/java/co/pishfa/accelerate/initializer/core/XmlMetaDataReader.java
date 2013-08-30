package co.pishfa.accelerate.initializer.core;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.pishfa.accelerate.initializer.api.InitializerFactory;
import co.pishfa.accelerate.initializer.model.InitEntityMetaData;
import co.pishfa.accelerate.initializer.model.InitPropertyMetaData;

/**
 * Reads {@link InitEntityMetaData} from an XML cofig file.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class XmlMetaDataReader {

	private static final String INITIALIZER_METADATA_XSD = "/initializer-metadata.xsd";
	protected static final Namespace PISHFA_NS = Namespace.getNamespace("http://pishfa.co");

	private static final Logger log = LoggerFactory.getLogger(XmlMetaDataReader.class);

	protected InitializerFactory factory;

	public XmlMetaDataReader(InitializerFactory factory) {
		this.factory = factory;
	}

	public void processMetadata(InputStream input) throws Exception {
		Element root = getRootElement(input);
		for (Element entityElement : root.getChildren("entity", PISHFA_NS)) {
			InitEntityMetaData initEntity = processEntityElement(entityElement);
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

	protected InitEntityMetaData processEntityElement(Element entityElement) throws ClassNotFoundException {
		String entityClazz = entityElement.getAttributeValue("class");
		String entityAlias = entityElement.getAttributeValue("alias");
		String entityKey = entityElement.getAttributeValue("key");
		InitEntityMetaData initEntity = new InitEntityMetaData(entityAlias, Class.forName(entityClazz), entityKey);
		String inherits = entityElement.getAttributeValue("inherits");
		if (!StringUtils.isEmpty(inherits)) {
			for (String inherit : inherits.split(",")) {
				InitEntityMetaData inheritEntity = factory.getInitEntityByAlias(inherit);
				if (inheritEntity != null) {
					for (InitPropertyMetaData property : inheritEntity.getProperties()) {
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

	protected void processEntityElementProperties(Element entityElement, InitEntityMetaData initEntity) {
		// read properties of an entity
		for (Element propertyElemement : entityElement.getChildren("property", PISHFA_NS)) {
			InitPropertyMetaData initProperty = processPropertyElement(propertyElemement);
			initEntity.addProperty(initProperty);
		}
	}

	protected InitPropertyMetaData processPropertyElement(Element propertyElemement) {
		String propName = propertyElemement.getAttributeValue("name");
		String propAlias = propertyElemement.getAttributeValue("alias");
		String propDefault = propertyElemement.getAttributeValue("default");
		String propDynamic = propertyElemement.getAttributeValue("dynamic");
		InitPropertyMetaData initProperty = new InitPropertyMetaData(propName, propAlias, propDefault,
				!"false".equals(propDynamic));
		return initProperty;
	}

}

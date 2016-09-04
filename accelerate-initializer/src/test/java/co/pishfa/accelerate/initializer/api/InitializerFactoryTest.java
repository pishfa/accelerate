package co.pishfa.accelerate.initializer.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import co.pishfa.accelerate.initializer.model.*;
import org.junit.Before;
import org.junit.Test;

import co.pishfa.accelerate.initializer.model.InitPropertyMetaData;

public class InitializerFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test(expected = Exception.class)
	public void testNonConformingConfig() throws Exception {
		new InitializerFactory().metadata("test_bad_config_1.xml");
	}

	@Test(expected = Exception.class)
	public void testNoXsdConfig() throws Exception {
		new InitializerFactory().metadata("test_bad_config_2.xml");
	}

	@Test
	public void testXmlBasedMetadata() throws Exception {
		InitializerFactory factory = new InitializerFactory().metadata("test_config_1.xml");
		checkInitEntites(factory);
	}

	@Test
	public void testAnnotationBasedMetadata() throws Exception {
		InitializerFactory factory = new InitializerFactory().entityClasses(Author.class, Book.class, Category.class);
		checkInitEntites(factory);
	}

	private void checkInitEntites(InitializerFactory factory) {
		InitEntityMetaData author = factory.getInitEntityByAlias("Author");
		assertNotNull(author);
		assertEquals(Author.class, author.getEntityClass());

		InitEntityMetaData book = factory.getInitEntityByAlias("book");
		assertNotNull(book);
		assertEquals(Book.class, book.getEntityClass());
		assertEquals(4, book.getProperties().size());
		InitPropertyMetaData name = book.getProperty("title");
		assertNotNull(name);
		assertEquals("name", name.getName());

		InitPropertyMetaData fullName = book.getProperty("fullName");
		assertNotNull(fullName);
		assertEquals("Book #{this.name}", fullName.getDefaultValue());
	}
}

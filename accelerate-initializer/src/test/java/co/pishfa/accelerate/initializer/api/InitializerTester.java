/**
 * 
 */
package co.pishfa.accelerate.initializer.api;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import co.pishfa.accelerate.initializer.model.Author;
import co.pishfa.accelerate.initializer.model.Book;
import co.pishfa.accelerate.initializer.model.Category;

/**
 * @author Taha Ghasemi
 * 
 */
public class InitializerTester {

	private static InitializerFactory factory;
	private InitListener listener;
	private Initializer initializer;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		factory = new InitializerFactory().entityClasses(Book.class, Author.class, Category.class).uniquePropertyName(
				"name");
	}

	@Before
	public void seUpBefore() {
		initializer = factory.create(listener);
	}

	/**
	 * Test method for {@link co.pishfa.accelerate.initializer.core.DefaultInitializer#read(java.io.File)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadFile() throws Exception {
		Author author = new Author();
		author.setName("taha");
		initializer.read("test_init_1.xml");
	}

	@Test
	public void testDynaRef() throws Exception {
		initializer.read("test_dyna_ref.xml");
		Map<String, Object> anchores = initializer.getAnchores();
		Author taha = (Author) anchores.get("Author:taha");
		Category cat = (Category) anchores.get("Category:cat");

		assertEquals(taha, getBook(anchores, "test0").getAuthor());
		assertEquals(cat, getBook(anchores, "test0").getCategory());

		assertEquals(taha, getBook(anchores, "test1").getAuthor());

		assertEquals(cat, getBook(anchores, "test2").getCategory());

		assertNull(getBook(anchores, "test3").getAuthor());

		assertNull(getBook(anchores, "test4").getAuthor());
		assertNull(getBook(anchores, "test4").getCategory());
	}

	protected Book getBook(Map<String, Object> anchores, String name) {
		return (Book) anchores.get("book:" + name);
	}
}

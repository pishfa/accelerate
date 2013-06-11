/**
 * 
 */
package co.pishfa.accelerate.initializer.api;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
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
	private Map<String, Object> anchores;

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
	public void testSimple() throws Exception {
		initializer.read("test_simple.xml");
		anchores = initializer.getAnchores();
		Author a1 = (Author) anchores.get("Author:a1");
		assertNotNull(a1);
		assertNotNull(anchores.get("Author:a2"));
		Category c1 = (Category) anchores.get("Category:c1");
		assertNotNull(c1);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String anchorName, Class<T> entityClass) {
		return (T) anchores.get(anchorName);
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

	@Test
	public void testEl() throws Exception {
		Map<String, Object> vars = new HashMap<>();
		vars.put("num", 5);
		initializer = factory.create(listener, vars);
		initializer.read("test_el.xml");
		Map<String, Object> anchores = initializer.getAnchores();
		Author taha = (Author) anchores.get("Author:taha");
		Category cat = (Category) anchores.get("Category:cat");

		assertEquals(taha, getBook(anchores, "test0").getAuthor());
		assertEquals(cat, getBook(anchores, "test0").getCategory());

		assertEquals("by taha", getBook(anchores, "test1").getFullName());
		assertEquals("test5", getBook(anchores, "test2").getFullName());
		assertEquals("test#{num}", getBook(anchores, "test3").getFullName());
	}

	@Test
	public void testFirstLevel() throws Exception {
		Map<String, List<Object>> result = initializer.read("test_first_levels.xml");
		assertEquals(2, result.size());
		assertNotNull(result.get("Authors"));
		assertNotNull(result.get("Author"));
		assertEquals(3, result.get("Authors").size());
		assertEquals(4, result.get("Author").size());
		assertEquals("t1", ((Author) result.get("Authors").get(0)).getName());
	}
}

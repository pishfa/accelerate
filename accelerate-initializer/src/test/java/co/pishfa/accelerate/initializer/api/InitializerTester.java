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

		Author a1 = initializer.getObject("Author:a1", Author.class);
		assertNotNull(a1);
		assertEquals("a1", a1.getName());

		Category c1 = initializer.getObject("Category:c1", Category.class);
		assertNotNull(c1);
		assertNull(c1.getCategory());

		Book b1 = initializer.getObject(":b1", Book.class);
		assertNotNull(b1);
		assertEquals("b1", b1.getName());
		assertEquals(a1, b1.getAuthor());
		assertEquals(c1, b1.getCategory());
		assertEquals("Book b1", b1.getFullName());

		Category c2 = initializer.getObject(":c2", Category.class);
		assertNotNull(c2);
		assertEquals(c1, c2.getCategory());
	}

	@Test
	public void testAssociation() throws Exception {
		initializer.read("test_association.xml");
		assertNotNull(initializer.getObject(":b1", Book.class).getAuthor());
		assertEquals(initializer.getObject(":a1", Author.class), initializer.getObject(":b1", Book.class).getAuthor());

		assertNotNull(initializer.getObject(":b2", Book.class).getAuthor());
		assertEquals(initializer.getObject(":a2", Author.class), initializer.getObject(":b2", Book.class).getAuthor());

		assertNotNull(initializer.getObject(":b3", Book.class).getAuthor());
		assertEquals(initializer.getObject(":a3", Author.class), initializer.getObject(":b3", Book.class).getAuthor());

		// assertNotNull(initializer.getObject(":b4", Book.class).getAuthor());
		// assertEquals(initializer.getObject(":a4", Author.class), initializer.getObject(":b4",
		// Book.class).getAuthor());
	}

	@Test
	public void testCompund() throws Exception {
		initializer.read("test_compound.xml");
		assertEquals("a1", initializer.getObject(":b1", Book.class).getMainAuthor().getName());
		// assertEquals("a2", initializer.getObject(":b2", Book.class).getMainAuthor().getName());
	}

	@Test
	public void testAnchor() throws Exception {
		initializer.read("test_anchors.xml");
		anchores = initializer.getAnchores();
		assertNotNull(anchores.get("a1"));
		assertNotNull(anchores.get("Author:a2"));
		assertNotNull(anchores.get("Author:a3"));
		assertNotNull(anchores.get("Author:a4"));
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

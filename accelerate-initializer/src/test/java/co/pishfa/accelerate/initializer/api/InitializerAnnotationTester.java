/**
 * 
 */
package co.pishfa.accelerate.initializer.api;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import co.pishfa.accelerate.initializer.data.TestSimple;
import co.pishfa.accelerate.initializer.model.Author;
import co.pishfa.accelerate.initializer.model.Book;
import co.pishfa.accelerate.initializer.model.Category;

/**
 * @author Taha Ghasemi
 * 
 */
public class InitializerAnnotationTester {

	private static InitializerFactory factory;
	private InitListener listener;
	private Initializer initializer;
	private Map<String, Object> anchores;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		factory = new InitializerFactory().entityClasses(Book.class, Author.class, Category.class).keyPropertyName(
				"name");
	}

	@Before
	public void seUpBefore() {
		initializer = factory.create(listener);
	}

	@Test
	public void testSimple() throws Exception {
		initializer.read(TestSimple.class);

		Author a1 = initializer.getObject(TestSimple.Authors.A1.class, Author.class);
		assertNotNull(a1);
		assertEquals("A1", a1.getName());

		Author a1_a = initializer.getObject("Author:A1", Author.class);

		assertEquals(a1, a1_a);

		Category c1 = initializer.getObject(TestSimple.C1.class, Category.class);
		assertNotNull(c1);
		assertNull(c1.getCategory());

		Book b1 = initializer.getObject(TestSimple.C1.B1.class, Book.class);
		assertNotNull(b1);
		assertEquals("b1", b1.getName());
		assertEquals(a1, b1.getAuthor());
		assertEquals(c1, b1.getCategory());
		assertEquals("Book b1", b1.getFullName());

		Category c2 = initializer.getObject(TestSimple.C1.C2.class, Category.class);
		assertNotNull(c2);
		assertEquals(c1, c2.getCategory());
	}

}

/**
 * 
 */
package co.pishfa.accelerate.initializer.api;

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
		factory = new InitializerFactory().entityClasses(Book.class, Author.class, Category.class);
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
		initializer.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("test_init_1.xml"));
	}
}

/**
 * 
 */
package co.pishfa.accelerate.initializer.xml;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

import co.pishfa.accelerate.initializer.InitEntityMetaData;
import co.pishfa.accelerate.initializer.InitListener;
import co.pishfa.accelerate.initializer.Initializer;
import co.pishfa.accelerate.initializer.InitializerFactory;

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
		factory = new InitializerFactory().configFile("test_config_1.xml").uniquePropertyName("name");
	}

	@Before
	public void seUpBefore() {
		listener = mock(InitListener.class);
		initializer = factory.create(listener);
	}

	@Test(expected = Exception.class)
	public void testNonConformingConfig() throws Exception {
		new InitializerFactory().configFile("test_bad_config_1.xml");
	}

	@Test(expected = Exception.class)
	public void testNoXsdConfig() throws Exception {
		new InitializerFactory().configFile("test_bad_config_2.xml");
	}

	/**
	 * Test method for {@link co.pishfa.accelerate.initializer.Initializer#read(java.io.File)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadFile() throws Exception {
		Author author = new Author();
		author.setName("taha");
		when(listener.findEntity(any(InitEntityMetaData.class), any(String[].class), any(Object[].class))).thenReturn(
				author);
		initializer.read(getClass().getResourceAsStream("/test_init_1.xml"));

		BaseMatcher<Book> fullNameMatcher = new TypeSafeMatcher<Book>() {

			@Override
			public void describeTo(Description arg0) {
			}

			@Override
			public boolean matchesSafely(Book item) {
				return item.getFullName().equals("Book test");
			}

		};
		verify(listener, atLeastOnce()).entityCreated(isA(InitEntityMetaData.class), argThat(fullNameMatcher));
		verify(listener, times(6)).entityCreated(isA(InitEntityMetaData.class), isA(Book.class));
	}
}

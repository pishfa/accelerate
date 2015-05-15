/**
 * 
 */
package co.pishfa.accelerate.convert;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import co.pishfa.accelerate.convert.DefaultConverter;

/**
 * @author Taha Ghasemi
 * 
 */
public class DefaultConverterTest {

	private DefaultConverter converter;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		converter = new DefaultConverter();
	}

	/**
	 * Test method for {@link co.pishfa.accelerate.convert.DefaultConverter#toString(java.lang.Object)}.
	 */
	@Test
	public void testToStringObject() {
		assertEquals("2.3", converter.toString(2.3f));
		assertEquals("2.3", converter.toString(2.3d));
		assertEquals("true", converter.toString(true));
	}

	/**
	 * Test method for {@link co.pishfa.accelerate.convert.DefaultConverter#toInteger(java.lang.Object)}.
	 */
	@Test
	public void testToInteger() {
		assertTrue(2 == converter.toInteger("2"));
		assertTrue(2 == converter.toInteger(2.3f));
		try {
			converter.toInteger("2s");
			fail();
		} catch (Exception e) {
		}
	}

	/**
	 * Test method for {@link co.pishfa.accelerate.convert.DefaultConverter#toLong(java.lang.Object)}.
	 */
	@Test
	public void testToLong() {
		assertTrue(2L == converter.toLong("2"));
		assertTrue(2L == converter.toLong(2));
	}

	/**
	 * Test method for {@link co.pishfa.accelerate.convert.DefaultConverter#toFloat(java.lang.Object)}.
	 */
	@Test
	public void testToFloat() {
		assertTrue(2.3f == converter.toFloat("2.3"));
	}

	/**
	 * Test method for {@link co.pishfa.accelerate.convert.DefaultConverter#toBoolean(java.lang.Object)}.
	 */
	@Test
	public void testToBoolean() {
		assertTrue(converter.toBoolean("true"));
		assertFalse(converter.toBoolean("false"));
	}

	/**
	 * Test method for {@link co.pishfa.accelerate.convert.DefaultConverter#toObject(java.lang.Object, java.lang.Class)}.
	 */
	@Test
	public void testToObject() {
		assertTrue(2 == converter.toObject("2", Integer.class));
	}

	public enum TestConvertEnum {
		E1,
		E2
	};

	/**
	 * Test method for {@link co.pishfa.accelerate.convert.DefaultConverter#toEnum(java.lang.Object, java.lang.Class)}.
	 */
	@Test
	public void testToEnum() {
		assertEquals(TestConvertEnum.E1, converter.toEnum("E1", TestConvertEnum.class));
	}

}

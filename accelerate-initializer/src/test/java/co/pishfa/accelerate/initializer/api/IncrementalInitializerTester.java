package co.pishfa.accelerate.initializer.api;

import java.util.Map;

import static org.junit.Assert.*;

import co.pishfa.accelerate.initializer.model.*;
import org.junit.Test;

import co.pishfa.accelerate.initializer.core.BaseInitListener;
import co.pishfa.accelerate.initializer.model.InitEntityMetadata;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class IncrementalInitializerTester {

	@Test
	public void test() throws Exception {
		final Author a1 = new Author();
		final Category c1 = new Category();
		InitializerFactory factory = new InitializerFactory()
				.entityClasses(Book.class, Author.class, Category.class, Tag.class).key("name").incremental(true);
		Initializer initializer = factory.create(new BaseInitListener() {
			@Override
			public Object findEntity(InitEntityMetadata initEntity, Map<String, Object> propValues) {
				if ("a1".equals(propValues.get("name")))
					return a1;
				if ("c1".equals(propValues.get("name")))
					return c1;
				return null;
			}
		});

		initializer.read("test_incremental.xml");
		assertEquals(a1, initializer.getObject("a1", Author.class));
		assertEquals(c1, initializer.getObject("c1", Category.class));
		assertNotNull(initializer.getObject("a2", Author.class));
	}

}

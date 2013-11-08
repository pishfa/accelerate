package co.pishfa.accelerate.initializer.data;

import co.pishfa.accelerate.initializer.annotation.Author;
import co.pishfa.accelerate.initializer.annotation.Book;
import co.pishfa.accelerate.initializer.annotation.Category;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public interface TestSimple {

	interface Authors {
		@Author
		interface A1 {
		}

		@Author
		interface A2 {
		}
	}

	@Category
	interface C1 {

		@Book(title = "b1", edition = "2", author = Authors.A1.class)
		interface B1 {
		}

		@Category
		interface C2 {
			@Book(title = "b2")
			interface B2 {
			}
		}
	}

}

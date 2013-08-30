package co.pishfa.accelerate.initializer.data;

import co.pishfa.accelerate.initializer.annotation.Author;
import co.pishfa.accelerate.initializer.annotation.Book;
import co.pishfa.accelerate.initializer.annotation.Category;

public interface TestSimple {

	interface Authors {
		@Author(name = "a1")
		interface A1 {
		}

		@Author(name = "a2")
		interface A2 {
		}
	}

	@Category(name = "c1")
	interface C1 {
		@Book(title = "b1", edition = "2", author = Authors.A1.class)
		interface B1 {
		}

		@Category(name = "c2")
		interface C2 {
			@Book(title = "b2")
			interface B2 {
			}
		}
	}

}

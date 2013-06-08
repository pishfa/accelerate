/**
 * 
 */
package co.pishfa.accelerate.initializer.model;

import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;

/**
 * @author Taha Ghasemi
 * 
 */
@InitEntity(alias = "book")
public class Book {

	@InitProperty(dynamic = false, alias = "title")
	private String name;
	private Author author;
	private Category category;

	@InitProperty("Book #{this.title}")
	private String fullName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}
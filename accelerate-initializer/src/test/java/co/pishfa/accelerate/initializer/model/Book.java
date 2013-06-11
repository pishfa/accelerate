/**
 * 
 */
package co.pishfa.accelerate.initializer.model;

import java.util.List;

/**
 * @author Taha Ghasemi
 * 
 */
@InitEntity(alias = "book", unique = "name")
public class Book {

	@InitProperty(dynamic = false, alias = "title")
	private String name;
	private Author author;
	@InitProperty("@parent?")
	private Category category;

	@InitProperty("Book #{this.name}")
	private String fullName;

	private List<Author> authors;

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

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

}
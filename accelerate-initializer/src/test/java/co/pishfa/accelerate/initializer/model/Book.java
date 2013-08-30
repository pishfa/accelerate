/**
 * 
 */
package co.pishfa.accelerate.initializer.model;

import java.util.List;

/**
 * @author Taha Ghasemi
 * 
 */
@InitEntity(alias = "book")
public class Book {

	@InitProperty(dynamic = false, alias = "title")
	@InitKey
	private String name;
	private Author author;
	@InitProperty("@parent?")
	private Category category;

	@InitProperty("Book #{this.name}")
	private String fullName;

	private List<Author> authors;

	private Author mainAuthor = new Author();

	private int edition;

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

	public int getEdition() {
		return edition;
	}

	public void setEdition(int edition) {
		this.edition = edition;
	}

	public Author getMainAuthor() {
		return mainAuthor;
	}

	public void setMainAuthor(Author mainAuthor) {
		this.mainAuthor = mainAuthor;
	}

}
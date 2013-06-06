/**
 * 
 */
package co.pishfa.accelerate.initializer.noannotation;


/**
 * @author Taha Ghasemi
 * 
 */
public class Book {

	private String name;
	private Author author;
	private Category category;
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
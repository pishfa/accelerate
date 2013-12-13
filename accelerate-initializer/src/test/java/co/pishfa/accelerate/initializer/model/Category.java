/**
 * 
 */
package co.pishfa.accelerate.initializer.model;

import java.util.List;

/**
 * @author Taha Ghasemi
 * 
 */
@InitEntity(properties = { @InitProperty(name = "_in-parent_", value = "children?") })
public class Category {

	private String name;
	@InitProperty("@parent?")
	private Category category;

	private List<Book> books;
	private List<Category> children;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

	public List<Category> getChildren() {
		return children;
	}

	public void setChildren(List<Category> children) {
		this.children = children;
	}

}

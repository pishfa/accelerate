/**
 * 
 */
package co.pishfa.accelerate.initializer.model;

/**
 * @author Taha Ghasemi
 * 
 */
public class Category {

	private String name;
	@InitProperty("@parent?")
	private Category category;

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

}

/**
 * 
 */
package co.pishfa.accelerate.initializer.model;

/**
 * @author Taha Ghasemi
 * 
 */
@InitEntity
public class Author {

	@InitKey
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

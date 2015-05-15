/**
 * 
 */
package co.pishfa.security.entity.authorization;

/**
 * Indicates the degree of changeability
 * 
 * @author Taha Ghasemi
 * 
 */
public enum AccessLevel {

	INVISIBLE("security.access.invisible", 0), READ_ONLY("security.access.read_only", 1), READ_WRITE("security.access.read_write", 2), READ_WRITE_DELETE(
			"security.access.read_write_delete", 3);

	private String name;
	private int level;

	private AccessLevel(String name, int level) {
		this.name = name;
		this.level = level;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

}

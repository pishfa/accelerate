/**
 * 
 */
package co.pishfa.security.entity.authorization;

/**
 * Specifies the importance of an entity. Users only have access to entities
 * whose level are lower or equal than the current security level of the user.
 * 
 * @author Taha Ghasemi
 * 
 */
public enum SecurityLevel {

	UNCLASSIFIED("security.level.unclassified", 0), RESTRICTED("security.level.restricted", 1), CONFIDENTIAL("security.level.confidential", 2), SECRET(
			"security.level.secret", 3), TOP_SECRET("security.level.top_secret", 4);

	private String name;
	private int level;

	private SecurityLevel(String name, int level) {
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

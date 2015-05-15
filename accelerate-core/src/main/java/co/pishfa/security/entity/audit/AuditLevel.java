package co.pishfa.security.entity.audit;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
public enum AuditLevel {

	INFO("level.info", 0), WARN("level.warn", 1), RISK("level.risk", 2), CRITICAL("level.critical", 3);

	private String name;
	private int order;

	private AuditLevel(String name, int order) {
		this.name = name;
		this.order = order;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

}

package co.pishfa.accelerate.persistence.filter;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class FilterFieldMetadata {
	private FilterFieldType type;
	private String path;
	private FilterFieldCondition condition;
    private boolean cleanable;

	public FilterFieldMetadata(FilterFieldType type, String name, FilterFieldCondition condition, boolean cleanable) {
		this.type = type;
		this.path = name;
		this.condition = condition;
        this.cleanable = cleanable;
	}

    public boolean isCleanable() {
        return cleanable;
    }

    public FilterFieldType getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public FilterFieldCondition getCondition() {
		return condition;
	}

    public void setType(FilterFieldType type) {
        this.type = type;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCondition(FilterFieldCondition condition) {
        this.condition = condition;
    }

}

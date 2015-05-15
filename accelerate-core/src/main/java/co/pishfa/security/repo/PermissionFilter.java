package co.pishfa.security.repo;

import co.pishfa.accelerate.persistence.filter.SimpleFilter;
import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.entity.authentication.Principal;
import co.pishfa.security.entity.authorization.Permission;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
public class PermissionFilter extends SimpleFilter<Permission> {

	private final Principal principal;
	private String action;

	public PermissionFilter(String viewAction, Principal principal) {
		super(viewAction);
		this.principal = principal;
	}

	@Override
	public void addConditions(QueryBuilder<Permission> query) {
		super.addConditions(query);
		query.append("and e.principalId = :pid and e.principalType = :pType ").with("pid", principal.getId())
				.with("pType", principal.getMetadata());
		if (!StrUtils.isEmpty(action)) {
			query.append("and definition.action.title like :action ").with("action", action + "%");
		}
	}

	@Override
	public void clean() {
		action = null;
	}

	@Override
	public boolean isClean() {
		return StrUtils.isEmpty(action);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}

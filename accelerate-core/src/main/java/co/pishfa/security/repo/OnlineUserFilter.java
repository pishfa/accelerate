package co.pishfa.security.repo;

import co.pishfa.accelerate.persistence.filter.SimpleFilter;
import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.entity.authentication.OnlineUser;

/**
 * 
 * @author Ghasemi
 * 
 */
public class OnlineUserFilter extends SimpleFilter<OnlineUser> {

	public OnlineUserFilter(String viewAction) {
		super(viewAction);
	}

	private String username;

	@Override
	public void addConditions(QueryBuilder<OnlineUser> query) {
		super.addConditions(query);
		if (!StrUtils.isEmpty(username)) {
			query.append(" and e.user.name like :username ").with("username", '%' + username + '%');
		}
	}

	@Override
	public void clean() {
		username = null;
	}

	@Override
	public boolean isClean() {
		return StrUtils.isEmpty(username);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}

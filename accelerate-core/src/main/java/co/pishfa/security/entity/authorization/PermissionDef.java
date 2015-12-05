/**
 * 
 */
package co.pishfa.security.entity.authorization;

import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

/**
 * Associates a permission scope to an action such as city.view:all, city.delete:domain, city.edit:custom,
 * user_defined1:all
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_permission_def")
@NamedQueries({
		@NamedQuery(name = "PermissionDef.findByActionAndScopeTitle", query = "select e from PermissionDef e where e.action.title = ?1 and e.scope.title = ?2"),
		@NamedQuery(name = "PermissionDef.findByActionAndScope", query = "select e from PermissionDef e where e.action.name = ?1 and e.scope.name = ?2") })
@Cacheable
@InitEntity(properties = {
		@InitProperty(name = "action", value = "@parent(1)") /* Note that we should declare it here instead of using InitProperty since we want it in name */,
		@InitProperty(name = "name", value = "#{this.action.name}:#{this.scope.name}") })
public class PermissionDef extends AccessRuleDef {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	private Action action;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	private PermissionScope scope;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "definition")
	@MapKey(name = "name")
	private Map<String, PermissionDefParam> parameters;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(joinColumns = @JoinColumn(name="permissiondef_id"))
	private List<PermissionDef> include;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "definition")
	private List<Permission> permissions;

	public PermissionDef() {
	}

	public PermissionDef(Action action, PermissionScope scope) {
		this.action = action;
		this.scope = scope;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public PermissionScope getScope() {
		return scope;
	}

	public void setScope(PermissionScope rule) {
		this.scope = rule;
	}

	public PermissionDefParam getParam(String name) {
		return parameters.get(name);
	}

	/**
	 * @return the parameters
	 */
	public Map<String, PermissionDefParam> getParameters() {
		return parameters;
	}

	public List<PermissionDef> getInclude() {
		return include;
	}

	public void setInclude(List<PermissionDef> include) {
		this.include = include;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

}
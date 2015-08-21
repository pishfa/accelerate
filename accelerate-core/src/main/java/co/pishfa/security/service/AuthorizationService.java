package co.pishfa.security.service;

import co.pishfa.accelerate.cache.Cache;
import co.pishfa.accelerate.cache.CacheService;
import co.pishfa.accelerate.cache.NamedCached;
import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.core.FrameworkExtension;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.entity.authentication.Domain;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authentication.Principal;
import co.pishfa.security.entity.authentication.User;
import co.pishfa.security.entity.authorization.*;
import co.pishfa.security.repo.ActionRepo;
import co.pishfa.security.repo.PermissionDefRepo;
import co.pishfa.security.repo.PermissionRepo;
import co.pishfa.security.repo.RoleAssignmentRepo;
import co.pishfa.security.service.handler.ActionHandler;
import co.pishfa.security.service.handler.BlockScopeHandler;
import co.pishfa.security.service.handler.PermissionScopeHandler;
import co.pishfa.security.service.handler.ScopeHandler;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Service
public class AuthorizationService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

	@Inject
	private PermissionRepo permissionRepo;

	@Inject
	private PermissionDefRepo permissionDefRepo;

	@Inject
	private RoleAssignmentRepo roleAssignmentRepo;

	@Inject
	private ActionRepo actionRepo;

	@Inject
	private Instance<PermissionScopeHandler> scopeHandlersInstance;

	private final BlockScopeHandler blockScopeHandler = new BlockScopeHandler();

    @Inject
    private CacheService cacheService;
	/**
	 * Currently, only contains permissions of {@link co.pishfa.security.entity.authentication.Domain}.
	 */
	//@Inject
	//@NamedCache
	private Cache<Principal, Map<String, Permission>> principalPermissions;

	private final Map<String, ActionHandler> actionHandlers;

	private final Map<String, PermissionScopeHandler<?>> scopeHandlers;

    private final Map<String, Action> actionsByName;

	public static AuthorizationService getInstance() {
		return CdiUtils.getInstance(AuthorizationService.class);
	}

	public AuthorizationService() {
		actionHandlers = new HashMap<>(51);
		scopeHandlers = new HashMap<>(51);
        actionsByName = new HashMap<>(201);
	}

    @PostConstruct
	public void init() {
		for(PermissionScopeHandler scopeHandler : scopeHandlersInstance) {
			ScopeHandler annotation = scopeHandler.getClass().getAnnotation(ScopeHandler.class);
			if(annotation != null) {
				String scope = annotation.value();
				add(scope, scopeHandler);
			}
        }
        principalPermissions = cacheService.getCache("principalPermissions");
        for(Action action : actionRepo.findAll()) {
            actionsByName.put(action.getName(), action);
        }
	}

	public void add(String action, ActionHandler handler) {
		actionHandlers.put(action, handler);
	}

	public void add(String scope, PermissionScopeHandler<?> handler) {
        scopeHandlers.put(scope, handler);
	}

	/**
	 * @return the appropriate scope handler. If permission is null, a blocking scope handler will be returned.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <E> PermissionScopeHandler<E> getScopeHandler(Permission permission) {
		if (permission != null)
			return getScopeHandler(permission.getDefinition().getScope());
		else
			return (PermissionScopeHandler) blockScopeHandler;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <E> PermissionScopeHandler<E> getScopeHandler(PermissionScope scope) {
		return scope == null ? null : (PermissionScopeHandler) scopeHandlers.get(scope.getName());
	}

	public ActionHandler getActionHandler(String action) {
		return actionHandlers.get(action);
	}

    @NamedCached("userImpliedRoles")
	public Map<String, Role> getImpliedRoles(User user) {
		Map<String, Role> result = new HashMap<>();
		List<Long> principals = addPrincipals(user);
		addPrincipalRoles(principals, result);
		return result;
	}

	protected List<Long> addPrincipals(User user) {
		List<Long> principals = new ArrayList<>(8);
		Domain secDomain = user.getDomain();
		while (secDomain != null) {
			principals.add(0, secDomain.getId()); // should have less priority
													// (be overridden by next
													// levels)
			secDomain = secDomain.getDomain();
		}
		principals.add(user.getId());
		return principals;
	}

	protected void addPrincipalRoles(List<Long> principals, Map<String, Role> result) {
		List<RoleAssignment> userRoles = roleAssignmentRepo.findByPrincipals(principals);
		for (RoleAssignment ra : userRoles) {
			if (ra.isActive()) {
				addRole(result, ra.getRole());
			}
		}
	}

	protected void addRole(Map<String, Role> result, Role role) {
		result.put(role.getName(), role);
		if (role.getRoles() != null) {
			for (Role r : role.getRoles()) {
				addRole(result, r);
			}
		}
	}

    public Map<String, Permission> computeInheritedPermissions(Principal principal) {
        Map<String, Permission> res = new HashMap<>();
        computeInheritedPermissions(principal, res);
        return res;
    }

    //TODO it is to some extend copy of getImpliedPermissions
    //TODO support denied permissions
    private void computeInheritedPermissions(Principal principal, Map<String, Permission> res) {
        if(principal == null)
            return;

        switch(principal.getType()) {
            case USER:
                computeInheritedPermissions(principal.getDomain(), res);
                addPrincipalPermissions(principal.getDomain(), res);
                Map<String, Role> roles = getImpliedRoles((User) principal);
                for(Role role : roles.values()) {
                    if(role.getRepresentative() != null) {
                        addPrincipalPermissions(principal, res);
                    }
                }
                break;
            case DOMAIN:
                computeInheritedPermissions(principal.getDomain(), res); //parent
                break;
        }
    }

	@NamedCached("userImpliedPermissions")
	public Map<String, Permission> getImpliedPermissions(User user) {
		Map<String, Permission> result = new HashMap<>(1000 + 1); // also contains denied permissions
		addDomainPermissions(user.getDomain(), result);
		addPrincipalPermissions(user, result);

        Map<String, Role> roles = getImpliedRoles(user);
        for(Role role : roles.values()) {
            if(role.getRepresentative() != null) {
                addPrincipalPermissions(role.getRepresentative(), result);
            }
        }

		// filter denied permissions
		Map<String, Permission> finalResult = new HashMap<>(result.size() * 2 + 1);
		for (Entry<String, Permission> entry : result.entrySet()) {
			if (entry.getValue().getType().isAllow()) {
				finalResult.put(entry.getKey(), entry.getValue());
			}
		}

		return finalResult;
	}

	protected void addDomainPermissions(Domain domain, Map<String, Permission> result) {
		if (domain != null) {
			Map<String, Permission> cachedResult = principalPermissions.getIfPresent(domain);
			if (cachedResult != null) {
				result.putAll(cachedResult);
				return;
			}
			addDomainPermissions(domain.getParent(), result); // parent
																			// takes
																			// precedence
			addPrincipalPermissions(domain, result);
			principalPermissions.put(domain, result);
		}
	}

	protected void addPrincipalPermissions(Principal principal, Map<String, Permission> result) {
		List<Permission> list = permissionRepo.findByPrincipal(principal); //ordered by precedence
		for (Permission permission : list) {
			if (permission.isActive()) {
				PermissionDef definition = permission.getDefinition();
				Principal representative = definition.getRepresentative();
				if (representative != null) { // user defined permission
					addPrincipalPermissions(representative, result);
				} else {
					addPermission(permission, result);
					addIncludes(permission, definition, result);
				}
			}
		}
	}

	protected void addIncludes(Permission permission, PermissionDef definition, Map<String, Permission> result) {
		if (definition.getInclude() != null) {
			for (PermissionDef include : definition.getInclude()) {
				addPermission(new Permission(include, permission.getPrincipal()), result);
				addIncludes(permission, include, result);
			}
		}
		// find the children of action with the same scope and add them
		List<PermissionDef> children = permissionDefRepo.findDescentsByActionAndScope(definition.getAction(),
				definition.getScope());
		if (children != null) {
			for (PermissionDef include : children) {
				addPermission(new Permission(include, permission.getPrincipal()), result);
				addIncludes(permission, include, result);
			}
		}
	}

	protected void addPermission(Permission permission, Map<String, Permission> result) {
		Action action = permission.getDefinition().getAction();
		String key = action.getName();
		Permission old = result.get(key);
		if (overrides(permission, old)) {
			result.put(key, permission);
		}

	}

	protected boolean overrides(Permission permission, Permission old) {
		if (old != null && old.getType().isVeto()) { // veto in above hierarchy
														// takes precedence
			return false;
		}
		return true;
	}

	public Permission findPermission(Identity identity, String action) {
		Map<String, Permission> permissions = getInstance().getImpliedPermissions(identity.getUser());
		Permission permission = null;
		String refinedAction = action;
		do {
			permission = permissions.get(refinedAction);
		} while (permission == null && (refinedAction = generalizeAction(refinedAction)) != null);
		return permission;
	}

	public boolean hasPermission(final Identity identity, final Object target, final String action) {
		Validate.notNull(identity);

		if (StrUtils.isEmpty(action)) {
			return true;
		}

		Permission permission = findPermission(identity, action);
		if (permission != null) {
			if (target == null) {
				return true;
			}
			PermissionScopeHandler<Object> handler = getScopeHandler(permission.getDefinition().getScope());
			if (handler != null) {
				return handler.check(identity, target, action, permission);
			}
			return false;
		} else {
			ActionHandler handler = getActionHandler(action);
			if (handler != null) {
				return handler.check(identity, target, action);
			}
			// check action is defined or not...
			try {
				actionRepo.findByName(action);
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Action " + action + " is undefined and no suitable action handler can be found.");
			}
			return false;
		}
	}

	/**
	 * Given an action like a.b.c it converts it into a.b.*. Also a converts to *.
	 * 
	 * @return null if action already equals with *
	 */
	protected String generalizeAction(String action) {
        if("*".equals(action))
            return null;
		if (action.endsWith(".*"))
			action = action.substring(0, action.length() - ".*".length());
		int index = action.lastIndexOf('.');
		if (index < 0)
			return "*";
		else
			return action.substring(0, index) + ".*";
	}

    /**
     * Finds the action with the given name, or its generalization name.
     * @return null if not found.
     */
    public Action findAction(String name) {
        if(name == null)
            return null;
        Action action = null;
        do {
            action = actionsByName.get(name);
        } while (action == null && (name = generalizeAction(name)) != null);
        return action;
    }

    public void invalidatePrincipalPermissions(Principal principal) {
        Cache permCache = cacheService.getCache("userImpliedPermissions");
        permCache.removeAll();
        Cache roleCache = cacheService.getCache("userImpliedRoles");
        roleCache.removeAll();
        principalPermissions.removeAll();
    }

}

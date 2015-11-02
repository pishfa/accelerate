package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.convert.Converter;
import co.pishfa.accelerate.convert.ObjectConverter;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.message.Messages;
import co.pishfa.accelerate.message.UserMessages;
import co.pishfa.accelerate.meta.domain.EntityMetadataService;
import co.pishfa.accelerate.meta.entity.EntityMetadata;
import co.pishfa.accelerate.reflection.ReflectionUtils;
import co.pishfa.accelerate.service.EntityService;
import co.pishfa.accelerate.ui.controller.PageController;
import co.pishfa.accelerate.ui.controller.ViewController;
import co.pishfa.security.entity.authorization.Action;
import co.pishfa.security.service.AuthorizationService;
import org.apache.commons.lang3.Validate;

import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * Base for all controllers that control instances of an {@link Entity}. It manages controller options and provide basic
 * CURD methods plus their security checks.
 *
 * @param <T> type of entities of this controller
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public abstract class EntityController<T extends Entity<K>, K> extends PageController {

    private static final long serialVersionUID = 1L;

    /**
     * An id converter based on general converter.
     */
    private class IdConverter implements ObjectConverter<K> {

        @Override
        public String toString(K value) {
            return converter.toString(value);
        }

        @Override
        public K toObject(String value) {
            return converter.toObject(value, entityMetadata.getKeyClass());
        }
    }

    @Inject
    private UserMessages userMessages;

    @Inject
    private Messages messages;

    @Inject
    private Converter converter;

    private IdConverter idConverter;

    private EntityMetadata<T, K> entityMetadata;

    private final Map<EntityControllerOption, Object> options = new HashMap<>();

    @Inject
    private AuthorizationService authorizationService;

    @SuppressWarnings("unchecked")
    public EntityController() {
        ParameterizedType type = ReflectionUtils.getParameterizedSuperClass(getClass());
        if (type == null || type.getActualTypeArguments().length < 1) {
            getLogger().warn(
                    "Can not automatically detect the entity type from the generics arguments. Class: " + getClass());
        } else {
            Class<T> entityClass = (Class<T>) type.getActualTypeArguments()[0];
            //TODO
            construct(entityClass, (Class<K>) Long.class);
        }
    }

    public EntityController(Class<T> entityClass, Class<K> keyClass) {
        construct(entityClass, keyClass);
    }

    private void construct(Class<T> entityClass, Class<K> keyClass) {
        entityMetadata = EntityMetadataService.getInstance().getEntityMetadata(entityClass, keyClass);
        setDefaultOptions();
        UiControllerOptions o = ReflectionUtils.getDerivedAnnotation(getClass(), UiControllerOptions.class);
        if(o != null) {
            if(!"".equals(o.pageSize())) {
                setOption(EntityControllerOption.PAGE_SIZE, Integer.parseInt(o.pageSize()));
            }
            if(!"".equals(o.add())) {
                setOption(EntityControllerOption.ADD, Boolean.parseBoolean(o.add()));
            }
            if(!"".equals(o.edit())) {
                setOption(EntityControllerOption.EDIT, Boolean.parseBoolean(o.edit()));
            }
            if(!"".equals(o.delete())) {
                setOption(EntityControllerOption.ADD, Boolean.parseBoolean(o.delete()));
            }
            if(!"".equals(o.multiSelect())) {
                setOption(EntityControllerOption.MULTI_SELECT, Boolean.parseBoolean(o.multiSelect()));
            }
            if(!"".equals(o.id())) {
                setOption(EntityControllerOption.ID, o.id());
            }
            if(!"".equals(o.outcome())) {
                if("null".equals(o.outcome()))
                    setOption(EntityControllerOption.OUTCOME, null);
                else
                    setOption(EntityControllerOption.OUTCOME, o.outcome());
            }
            if(!"".equals(o.viewAction())) {
                setOption(EntityControllerOption.VIEW_ACTION, o.viewAction());
            }
            if(!"".equals(o.sortAscending())) {
                setOption(EntityControllerOption.SORT_ASCENDING, Boolean.parseBoolean(o.sortAscending()));
            }
            if(!"".equals(o.sortOn())) {
                setOption(EntityControllerOption.SORT_ON, o.sortOn());
            }
            if(!"".equals(o.secured())) {
                setOption(EntityControllerOption.SECURED, Boolean.parseBoolean(o.secured()));
            }
            if(!"".equals(o.autoReload())) {
                setOption(EntityControllerOption.AUTO_RELOAD, Boolean.parseBoolean(o.autoReload()));
            }
            if(!"".equals(o.local()))
                setOption(EntityControllerOption.LOCAL, Boolean.parseBoolean(o.local()));
        }
    }

    /**
     * Adds the default options before adding the specified options during construction.
     */
    protected void setDefaultOptions() {
        setOption(EntityControllerOption.SECURED, !getIdentity().shouldBypassSecurity());
    }

    protected void setOptions(Object... options) {
        for(int i = 0; i < options.length; i+=2) {
            setOption((EntityControllerOption) options[i], options[i+1]);
        }
    }

    protected void setOptions(Map<EntityControllerOption, Object> options) {
        for (Map.Entry<EntityControllerOption,Object> option : options.entrySet()) {
            this.options.put(option.getKey(), option.getValue());
        }
    }

    /**
     * Determines whether the controller has the given option or not.
     */
    public boolean hasOption(EntityControllerOption option) {
        Object value = options.get(option);
        return value == null? false : (value instanceof Boolean)? ((Boolean) value) : true;
    }

    public Object getOption(EntityControllerOption option) {
        return options.get(option);
    }

    public Object setOption(EntityControllerOption option, Object value) {
        return options.put(option, value);
    }

    public Object addOption(EntityControllerOption option) {
        return options.put(option, true);
    }

    public Object removeOption(EntityControllerOption option) {
        return options.remove(option);
    }

    public EntityService<T, K> getEntityService() {
        return getEntityMetadata().getRepository();
    }

    public EntityMetadata<T, K> getEntityMetadata() {
        return entityMetadata;
    }

    /**
     * Returns the security action related to viewing of entities. It first tries to find it in the options, then to infer this from the current page
     * (only if this controller is the primarly controller of this page)
     * and if no page info is available consults the entity service.
     *
     * @return the security action related to viewing of entities
     */
    public String getViewAction() {
        String viewAction = (String) getOption(EntityControllerOption.VIEW_ACTION);
        if(viewAction != null)
            return viewAction;
        if (getPage() != null && getPage().getViewAction() != null && getPageMetadata().getPrimaryController() != null
                && this.getClass().isAssignableFrom(getPageMetadata().getPrimaryController().getControllerClass())) {
            return getPage().getViewAction();
        }
        return getEntityService().getAction("view");
    }

    /**
     * Checks whether the user has the view permission or not on the given entity.
     */
    protected void checkViewPermission(T entity) {
        check(entity, "view");
    }

    /**
     * Checks whether the user has the add permission or not on the given entity.
     */
    protected void checkAddPermission(T entity) {
        check(entity, "add");
    }

    /**
     * Checks whether the user has the edit permission or not on the given entity.
     */
    protected void checkEditPermission(T entity) {
        check(entity, "edit");
    }

    /**
     * Checks whether the user has the delete permission or not on the given entity.
     */
    protected void checkDeletePermission(T entity) {
        check(entity, "delete");
    }

    /**
     * @return the entity with the given id
     */
    protected T findEntity(K key) {
        return getEntityService().findById(key);
    }

    /**
     * Saves the given entity. By default it uses the entity service to do the saving. applyCurrent calls this method to
     * perform saving.
     *
     * @return The newly entity that saved.
     */
    protected T saveEntity(T entity) {
        Validate.notNull(entity, "Can not save a null entity");
        return entity.getId() == null ? getEntityService().add(entity) : getEntityService().edit(entity);
    }

    /**
     * Deletes the given entity. By default it uses the entity service to do the deleting.
     */
    protected void deleteEntity(T entity) {
        Validate.notNull(entity, "Can not delete a null entity");
        entity = getEntityService().loadById(entity.getId());
        getEntityService().delete(entity);
    }

    /**
     * Determines whether user can add entities or not.
     */
    public boolean canAdd() {
        return hasOption(EntityControllerOption.ADD)
                && can("add");
    }

    /**
     * Determines whether the given entity is editable or not.
     */
    public boolean canEdit(T entity) {
        return hasOption(EntityControllerOption.EDIT)
                && can(entity, "edit");
    }

    /**
     * Determines whether the given entity is deletable or not.
     */
    public boolean canDelete(T entity) {
        return hasOption(EntityControllerOption.DELETE)
                && can(entity, "delete");
    }

    /**
     * @return true if entity is not null and the current identity can do the given action on this entity.
     * It also returns true if this controller is not secured or the {@link #getAction(String)} returns null.
     */
    public boolean can(T entity, String action) {
        if(hasOption(EntityControllerOption.SECURED))
            return entity != null && (getAction(action) == null || getIdentity().can(entity, getAction(action)));
        else
            return true;
    }

    /**
     * @return true if the current identity can do the given action.
     */
    public boolean can(String action) {
        if(hasOption(EntityControllerOption.SECURED))
            return getAction(action) == null || getIdentity().can(null, getAction(action));
        else
            return true;
    }

    /**
     * Checks whether the user has the permission of the given action or not on the given entity.
     */
    public void check(T entity, String action) {
        if(hasOption(EntityControllerOption.SECURED))
            getIdentity().checkPermission(entity, getAction(action));
        else
            return;
    }

    /**
     * @return the security action related to this action. By default it calls the getAction method of entity service.
     */
    protected String getAction(String action) {
        if("view".equals(action))
            return getViewAction();
        else
            return getEntityService().getAction(action);
    }

    /**
     * @return a title for the given action name based on the title of action in security system. If no such action is found,
     * the default key will be used.
     */
    public String getActionTitle(String actionName, String defaultKey) {
        Action action = authorizationService.findAction(getAction(actionName));
        return action == null? messages.get(defaultKey) : action.getTitle();
    }

    /**
     * Creates a new instance of entity. By default it uses the {@link co.pishfa.accelerate.service.EntityService#newEntity()}
     */
    protected T newEntity() {
        try {
            return getEntityService().newEntity();
        } catch (Exception e) {
            getLogger().error("", e);
        }
        return null;
    }

    @Override
    public void onViewLoaded() throws Exception {
        super.onViewLoaded();
        load();
    }

    /**
     * Loads associated data for this controller. It is first time called from {@link ViewController#onViewLoaded()}.
     *
     * @return null
     */
    public String load() {
        return null;
    }

    public UserMessages getUserMessages() {
        if (userMessages != null)
            return userMessages;
        return UserMessages.getInstance();
    }

    /**
     * @return a converter that converts between id and its string representation.
     * By default, it uses the general {@link co.pishfa.accelerate.convert.Converter} mechanism to do this.
     */
    public ObjectConverter<K> getIdConverter() {
        if (idConverter == null) {
            idConverter = new IdConverter();
        }
        return idConverter;
    }

}

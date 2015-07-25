package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.cache.UiCached;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.ui.UiAction;
import co.pishfa.accelerate.ui.UiMessage;
import co.pishfa.accelerate.ui.controller.ViewController;
import co.pishfa.accelerate.ui.param.OptionalParams;
import co.pishfa.accelerate.ui.param.RequiredParams;
import co.pishfa.accelerate.ui.phase.PhaseId;
import co.pishfa.accelerate.ui.phase.UiPhaseAction;
import co.pishfa.security.entity.authorization.SecuredEntity;
import org.apache.commons.lang3.Validate;

import java.util.List;

/**
 * Manages a list of entities. It supports basic CRUD operations on these entities.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 * @param <T>
 *            type of entities of this controller
 * 
 */
public class EntityMgmt<T extends Entity<K>, K> extends EntityPagedList<T, K> {

	private static final long serialVersionUID = 1L;

	private static final String ID_PARAM_NAME = "id";

	private Boolean editMode;

	public EntityMgmt(Class<T> entityClass, Class<K> keyClass) {
		super(entityClass, keyClass);
	}

	public EntityMgmt() {
		super();
	}

	@Override
	protected void setDefaultOptions() {
		super.setDefaultOptions();
        setOption(EntityControllerOption.ADD, true);
        setOption(EntityControllerOption.EDIT, true);
        setOption(EntityControllerOption.DELETE, true);
        setOption(EntityControllerOption.ID, ID_PARAM_NAME);
	}

	protected void loadCurrent() {
		String idParam = getIdParam();
		// if null is passed, it means we should use the current, no loading
		// required
		if (!NULL_KEY.equals(idParam)) {
			loadCurrent(getIdConverter().toObject(idParam));
		} else {
            reloadCurrent();
        }
	}

	protected String getIdParam() {
		return RequiredParams.getString(getIdParamName());
	}

	protected String getIdParamName() {
		return (String) getOption(EntityControllerOption.ID);
	}

	protected void loadCurrent(K id) {
		setCurrent(findEntity(id));
	}

    public boolean reload() {
        if(OptionalParams.getString(getIdParamName()) == null)
            reloadCurrent();
        return false;
    }

    @UiPhaseAction(value = PhaseId.RESTORE_VIEW, onPostback = true)
    public void onPostback() {
        if(hasOption(EntityControllerOption.AUTO_RELOAD))
            reload();
    }

	/**
	 * Reloads current based on its current id.
	 */
	protected void reloadCurrent() {
        if(getCurrent() != null && getCurrent().getId() != null)
		    loadCurrent(getCurrent().getId());
	}

	@Override
	public List<T> getSelected() {
		loadCurrent();
		return super.getSelected();
	}

	@Override
	public void onViewLoaded() throws Exception {
		clean();
		super.onViewLoaded();
	}

	@Override
	public String load() {
		setEditMode(null);
		return super.load();
	}

	/**
     * Calls {@link #newCurrent()}, then checks for the add permission of the current user by calling {@link #checkAddPermission(co.pishfa.accelerate.entity.common.Entity)}. After all these, {@link #addEdit()} is called.
	 *
	 * @return null
	 */
	@UiAction
	public String add() {
		if (hasOption(EntityControllerOption.ADD)) {
			setEditMode(false);
            newCurrent();
            checkAddPermission(getCurrent());
            addEdit();
		}
		return null;
	}

    /**
     * Creates a new entity (which is obtained by calling {@link #newEntity()}) and sets the current to it.
     */
    protected void newCurrent() {
        setCurrent(newEntity());
        if (getCurrent() instanceof SecuredEntity) {
            SecuredEntity securedEntity = (SecuredEntity) getCurrent();
            securedEntity.setCreatedBy(getIdentity().getUser());
            securedEntity.setDomain(getIdentity().getUser().getDomain());
        }
    }

    /**
	 * Prepares the current entity to be edited. This is done by first calling {@link #loadCurrent()}, checking the edit
	 * permission by calling {@link #checkEditPermission(co.pishfa.accelerate.entity.common.Entity)}, and then {@link #addEdit()}.
	 * 
	 * @return null
	 */
	@UiAction
	public String edit() {
		if (hasOption(EntityControllerOption.EDIT)) {
			setEditMode(true);
			loadCurrent();
            checkEditPermission(getCurrent());
            addEdit();
		}
		return null;
	}

    /**
     * Edits the given entity. This method can be used by external controllers to put this controller into edit mode
     * with a specific entity. No security check is performed.
     */
    public void edit(T entity) {
        setCurrent(entity);
        setEditMode(true);
        addEdit();
    }

    @UiAction
    public String detail() {
        setEditMode(null);
        loadCurrent();
        checkViewPermission(getCurrent());
        return null;
    }

    /**
	 * Called after either add or edit operations. Whether it is called from add or edit is determined by
	 * {@link #getEditMode()}. The entity under operation can be obtained via {@link #getCurrent()}. By default, this
	 * method set the parent of child controllers.
	 */
	@SuppressWarnings("unchecked")
	protected void addEdit() {
		if (getChildControllers() != null) {
			for (ViewController controller : getChildControllers()) {
				if (controller instanceof EntityChildMgmt) {
					// TODO check it is really T
					// TODO there might be multiple child controllers for different purposes (not only editing) so we
					// must add a scope to child controllers
					((EntityChildMgmt) controller).setParent(getCurrent());
				}
			}
		}
	}

	/**
	 * Saves the current entity. By default, it saves the entity by calling {@link #applyCurrent()} and then refresh the
	 * data by calling {@link #load()}.
	 * 
	 * @return null
	 */
	@UiAction
	@UiMessage
	public String save()  throws Exception {
		applyCurrent();
		load();
		return null;
	}

	/**
	 * 
	 * @return null
	 */
	@UiAction
	@UiMessage
	public String apply()  throws Exception{
		applyCurrent();
		load();
		return null;
	}

	/**
	 * Saves the current entity, and sets the current to the newly saved entity
	 */
	@SuppressWarnings("unchecked")
	protected void applyCurrent()  throws Exception {
		if (getChildControllers() != null) {
			for (ViewController controller : getChildControllers()) {
				if (controller instanceof EntityChildMgmt) {
					((EntityChildMgmt) controller).preCommit();
				}
			}
		}
		setCurrent(saveEntity(getCurrent()));
		if (getChildControllers() != null) {
			for (ViewController controller : getChildControllers()) {
				if (controller instanceof EntityChildMgmt) {
					((EntityChildMgmt) controller).commit(getCurrent());
				}
			}
		}
	}

	/**
	 * Deletes the selected entities (those returned from {@link #getSelected()}. For each selected entity, it checks
	 * for delete permission and then calls {@link #deleteEntity(Entity)}.
	 * 
	 * @return null
	 */
	@UiAction
	@UiMessage
	public String delete()  throws Exception {
		if (hasOption(EntityControllerOption.DELETE)) {
			for (T selected : getSelected()) {
                checkDeletePermission(selected);
				deleteEntity(selected);
			}
			load();
		}
		return null;
	}

	/**
	 * Deletes the given entity by first checking for security permission by calling {@link #checkDeletePermission(co.pishfa.accelerate.entity.common.Entity)} and then calling {@link #deleteEntity(Entity)}
	 * .
	 * 
	 * @return null
	 */
	@UiAction
	@UiMessage
	public String delete(T entity)  throws Exception {
		Validate.notNull(entity, "Can not delete a null entity");
		if (hasOption(EntityControllerOption.DELETE)) {
            checkDeletePermission(entity);
			deleteEntity(entity);
			load();
		}
		return null;
	}

	/**
	 * Cancels the current add/edit operation by clearing the current and edit mode.
	 * 
	 * @return null
	 */
	@UiAction
	public String cancel() {
		setEditMode(null);
		setCurrent(null);
		setCurrents(null);
		return null;
	}

	/**
	 * If we are in add/edit mode reset this entity to its initial state otherwise reset the filter.
	 */
	@Override
	public String reset() {
		if (getEditMode() != null) {
			if (getEditMode()) {
				reloadCurrent();
				addEdit();
			} else
				add();
			return null;
		} else
			return super.reset();
	}

	public Boolean getEditMode() {
		return editMode;
	}

    public boolean isEditMode() {
        return editMode != null && editMode == true;
    }

	public void setEditMode(Boolean editMode) {
		this.editMode = editMode;
	}

	@UiCached
	public String getEditTitle() {
		if (isEditMode()) {
			return getActionTitle("edit", "ui.page.title.edit");
		} else {
			return getActionTitle("add", "ui.page.title.add");
		}
	}

}

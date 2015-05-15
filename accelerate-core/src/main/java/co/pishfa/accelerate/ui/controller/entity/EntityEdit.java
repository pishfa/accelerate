package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.cache.UiCached;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.ui.UiAction;
import co.pishfa.accelerate.ui.UiMessage;
import co.pishfa.accelerate.ui.controller.ViewController;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.entity.authorization.SecuredEntity;

/**
 * This controller is suitable for pages that allow user to edit an entity. On load, it decides whether it should
 * operate in the edit mode (if an id is passed) or add mode. These modes are enabled by default but you can disable them
 * by providing your own options. The security permission is checked based on the add/edit mode.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class EntityEdit<T extends Entity<K>, K> extends EntityDisplay<T, K> {

	private static final long serialVersionUID = 1L;

	private boolean editMode;

	public EntityEdit(Class<T> entityClass, Class<K> keyClass) {
		super(entityClass, keyClass);
	}

	public EntityEdit() {
		super();
	}

	@Override
	protected void setDefaultOptions() {
		super.setDefaultOptions();
        setOption(EntityControllerOption.ADD, true);
        setOption(EntityControllerOption.EDIT, true);
        setOption(EntityControllerOption.DELETE, true);
        setOption(EntityControllerOption.OUTCOME, "ac:parent");
	}

	@Override
	public boolean canEdit(T entity) {
		return editMode && super.canEdit(entity);
	}

	@Override
	public boolean canDelete(T entity) {
		return editMode && super.canDelete(entity);
	}

    protected String getOutcome() {
        return (String) getOption(EntityControllerOption.OUTCOME);
    }

	/**
	 * If id param is given it loads the entity with this given id and goes to edit mode by calling {@link #edit()} (if edit option is set)
	 * otherwise it goes to add mode by calling {@link #add()}. This method is first time called from onViewLoaded().
     *
     * @return null
	 */
	@Override
	public String load() {
		if (!hasOption(EntityControllerOption.ADD) && !hasOption(EntityControllerOption.EDIT))
			return null;

		K id = null;
		try {
			id = getIdParam();
		} catch (IllegalStateException e) {
		}
		if (id == null) {
			if (hasOption(EntityControllerOption.ADD)) {
				add();
			} else {
				throw new IllegalStateException("Param " + getIdParam() + " is required for editing.");
			}
		} else {
			if (hasOption(EntityControllerOption.EDIT)) {
				edit();
			} else if (hasOption(EntityControllerOption.ADD)) {
				add();
			}
		}
		return null;
	}

	/**
	 * Calls {@link #newCurrent()}, then checks for the add permission of the current user by calling {@link #checkAddPermission(co.pishfa.accelerate.entity.common.Entity)}. After all these, {@link #addEdit()} is called.
	 */
	public void add() {
        setEditMode(false);
        newCurrent();
        checkAddPermission(getCurrent());
        addEdit();
	}

    /**
     * Creates a new entity (which is obtained by calling {@link #newEntity()}) and sets the current to it
     */
    protected void newCurrent() {
        setCurrent(newEntity());
        // TODO where should be this?
        if (getCurrent() instanceof SecuredEntity) {
            SecuredEntity securedEntity = (SecuredEntity) getCurrent();
            securedEntity.setCreatedBy(getIdentity().getUser());
            securedEntity.setDomain(getIdentity().getUser().getDomain());
        }
    }

    /**
	 * Prepares the current entity to be edited. This is done by first calling {@link #loadCurrent()}, checking for edit
	 * permission by calling {@link #checkEditPermission(co.pishfa.accelerate.entity.common.Entity)}, and then {@link #addEdit()}.
	 */
	public void edit() {
        setEditMode(true);
		loadCurrent();
        checkEditPermission(getCurrent());
        addEdit();
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

    /**
	 * Called after either add or edit operations. Whether it is called from add or edit is determined by
	 * {@link #isEditMode()}. The entity under operation can be obtained via {@link #getCurrent()}. By default, this
	 * method set the parent of child controllers.
	 */
	@SuppressWarnings("unchecked")
	protected void addEdit() {
		if (getChildControllers() != null) {
			for (ViewController controller : getChildControllers()) {
				if (controller instanceof EntityChildMgmt) {
					((EntityChildMgmt) controller).setParent(getCurrent());
				}
			}
		}
	}

	/**
	 * Saves the current entity. By default, it saves the entity by calling {@link #applyCurrent()}.
	 * 
	 * @return {@link #getOutcome()}
	 */
	@UiAction
	@UiMessage
	public String save() throws Exception {
		applyCurrent();
        setCurrent(null); //useful for cases where this controller is used in dialogs not pages
		return getOutcome();
	}

	/**
	 * Like {@link #save()}, saves the entity by calling {@link #applyCurrent()} but returns null so user will remain in the current page.
	 * 
	 * @return null
	 */
	@UiAction
	@UiMessage
	public String apply() throws Exception  {
		applyCurrent();
		return null;
	}

	/**
	 * Saves the current by calling saveEntity, and sets the current to the newly saved entity. Precommit and commit
	 * events are also propagated to child controllers.
	 */
	@SuppressWarnings("unchecked")
	public void applyCurrent() throws Exception  {
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
	 * Deletes the current entity by calling {@link #checkDeletePermission(co.pishfa.accelerate.entity.common.Entity)}
     * and then {@link #deleteCurrent()}.
	 * 
	 * @return {@link #getOutcome()}
	 */
	@UiAction
	@UiMessage
	public String delete() throws Exception  {
		if (hasOption(EntityControllerOption.DELETE)) {
            checkDeletePermission(getCurrent());
            deleteCurrent();
		}
		return getOutcome();
	}

    /**
	 * Deletes the current entity by calling {@link #deleteEntity(Entity)}.
	 */
	protected void deleteCurrent() {
		deleteEntity(getCurrent());
	}

	/**
	 * 
	 * @return {@link #getOutcome()}
	 */
	@UiAction
	public String cancel() {
        setCurrent(null);
		return getOutcome();
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	@Override
    @UiCached
    public String getPageTitle() {
        if (isEditMode()) {
            return StrUtils.defaultIfEmpty(super.getPageTitle(), getActionTitle("edit", "ui.page.title.edit"));
        } else {
            return StrUtils.defaultIfEmpty(super.getPageTitle(), getActionTitle("add", "ui.page.title.add"));
        }
    }

	/**
	 * Reset the value.
	 * 
	 * @return null
	 */
	@UiAction
	public String reset() {
		if (isEditMode()) {
			reloadCurrent();
			addEdit();
		} else
			add();
		return null;
	}

}

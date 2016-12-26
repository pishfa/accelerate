package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.cache.UiCached;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.ui.UiAction;
import co.pishfa.accelerate.ui.param.RequiredParams;
import org.apache.commons.lang3.Validate;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Can be used to manage children of an entity usually related with OneToMany or ManyToMany associations. This
 * controller must be used as a child controller of another controller (which is the controller of the parent entity).
 * No entity service operation is called during add, edit, or delete since it is assumed that the parent entity manages
 * these. The security of the operations is assumed to be checked by the parent. The parent is also responsible for
 * proper add/edit/delete operations. This usually means that the relation should be marked as cascade=CascadeType.All
 * and orphanRemoval=true. Note that in case of bidirectional ManyToMany, you should also manage the other side of the
 * relationship by proper implementation of the setParent method.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 * @param <T>
 *            type of entities of this controller
 * @param <P>
 *            type of parent entity
 * 
 */
public abstract class EntityChildMgmt<T extends Entity<Long>, P extends Entity<Long>> extends EntityList<T, Long> {

	private static final long serialVersionUID = 1L;

	private static final String ID_PARAM_NAME = "id";

	private long currentId;

	private Boolean editMode;

	private P parent;
	private List<T> added; // maintains newly added entities

	public EntityChildMgmt(Class<T> entityClass) {
		super(entityClass, Long.class);
	}

	public EntityChildMgmt() {
		super();
	}

	@Override
	protected void setDefaultOptions() {
		super.setDefaultOptions();
        setOption(EntityControllerOption.ADD, true);
        setOption(EntityControllerOption.EDIT, true);
        setOption(EntityControllerOption.DELETE, true);
        setOption(EntityControllerOption.LOCAL, true);
        setOption(EntityControllerOption.ID, ID_PARAM_NAME);
        setOption(EntityControllerOption.SECURED, false);
	}

	public P getParent() {
		return parent;
	}

	/**
	 * Sets the parent of this controller. Subsequently {@link #load()} is called.
	 */
	public void setParent(P parent) {
		this.parent = parent;
		load();
	}

	@Override
	protected List<T> findData() {
		List<T> res = findData(parent);
        if(res == null)
			res = new ArrayList<>();
		//Validate.notNull(res, "The returned value from findData should not be null");
		if (res.size() == 0) {
			currentId = 0;
		} else {
			currentId = res.get(0).getId();
			for (int i = 1; i < res.size(); i++) {
				if (res.get(i).getId() > currentId) {
					currentId = res.get(i).getId();
				}
			}
		}
		added = new ArrayList<>();
		return res;
    }

	/**
	 * @return the data associated to the passed parent. This method is called from {@link #findData()}.
	 */
	protected abstract List<T> findData(P parent);

	/**
	 * Called to set the parent of entity. Note that in case of delete the null is sent as the parent.
	 */
	protected abstract void setEntityParent(T entity, P parent);

	protected void loadCurrent() {
		String idParam = getIdParam();
		// if null is passed, it means we should use the current, no loading required
		if (!NULL_KEY.equals(idParam)) {
			loadCurrent(Long.parseLong(idParam));
		}
	}

	protected String getIdParam() {
		return RequiredParams.getString(getIdParamName());
	}

	protected String getIdParamName() {
        return (String) getOption(EntityControllerOption.ID);
	}

	protected void loadCurrent(long id) {
		setCurrent(findEntity(id));
	}

	/**
	 * Adds a new entity (which is obtained by calling {@link #newEntity()}) to the data and set the current to it. It
	 * also sets editMode and assigns an artificial id to the newly added entity. After all these, {@link #addEdit()} is
	 * called.
	 * 
	 * @return null
	 */
	@UiAction
	public String add() {
		if (hasOption(EntityControllerOption.ADD)) {
			editMode = false;
			T e = newEntity();
			add(e); // TODO we do this for inline table editings but for popups this is not necessary
			setCurrent(e);
			addEdit();
		}
		return null;
	}

	/**
	 * Directly adds the given entity to the data. The parent of entity is also set. This is usually useful for
	 * selection components.
	 */
	public void add(T entity) {
		Validate.notNull(entity, "Can not add a null entity");

		setEntityParent(entity, getParent());
		getData().add(entity);
		if (entity.getId() == null) {
			entity.setId(++currentId);
			added.add(entity);
		} else {
			// This is only necessary for mixture of calling this method with add method.
			if (currentId < entity.getId()) {
				currentId = entity.getId();
			}
			// change id of added entities to be greater than this entity id
			for (T e : added) {
				if (e.getId() <= entity.getId()) {
					e.setId(++currentId);
				}
			}
		}
	}

	/**
	 * Prepares the current entity to be edited. This is done by first calling {@link #loadCurrent()} and the
	 * {@link #addEdit()}.
	 * 
	 * @return null
	 */
	@UiAction
	public String edit() {
		if (hasOption(EntityControllerOption.EDIT)) {
			editMode = true;
			loadCurrent();
			addEdit();
		}
		return null;
	}

	public void edit(T entity) {
		editMode = true;
		setCurrent(entity);
		addEdit();
	}

	/**
	 * Called after either add or edit operations. Whether it is called from add or edit is determined by
	 * {@link #getEditMode()}. The entity under operation can be obtained via {@link #getCurrent()}.
	 */
	protected void addEdit() {
	}

	/**
	 * Saves the current entity. By default, it saves the entity by calling {@link #applyCurrent()}.
	 * 
	 * @return null
	 */
	@UiAction
	public String save() {
		applyCurrent();
		return null;
	}

	/**
	 * This method is called before the parent get saved.
	 */
	public void preCommit() {
		// remove artificial ids
		if(added != null)
			for (T entity : added) {
				entity.setId(null);
			}
	}

	/**
	 * This method is called when the parent is saved. The newly saved parent is passed as the parameter.
	 */
	public void commit(P attachedParrent) {
		Validate.notNull(attachedParrent, "Attached parent can not be null");
	}

	protected void applyCurrent() {
	}

	/**
	 * Deletes the current entity by first loading it using {@link #loadCurrent()} and then calling
	 * {@link #deleteCurrent()}.
	 * 
	 * @return null
	 */
	@UiAction
	public String delete() {
		if (hasOption(EntityControllerOption.DELETE)) {
			loadCurrent();
			deleteCurrent();
		}
		return null;
	}

	/**
	 * Deletes the given entity by calling {@link #deleteEntity(Entity)}.
	 * 
	 * @return null
	 */
	@UiAction
	public String delete(T entity) {
		Validate.notNull(entity, "Can not delete a null entity");
		if (hasOption(EntityControllerOption.DELETE)) {
			deleteEntity(entity);
		}
		return null;
	}

	/**
	 * Deletes the current entity by calling {@link #deleteEntity(Entity)} and then set it to null.
	 */
	protected void deleteCurrent() {
		deleteEntity(getCurrent());
		setCurrent(null);
	}

	/**
	 * Deletes the given entity from data and set its parent to null.
	 */
	@Override
	protected void deleteEntity(T entity) {
		Validate.notNull(entity, "Can not delete a null entity");
		getData().remove(entity);
		setEntityParent(entity, null);
	}

	/**
	 * Cancels the edit operation by removing the current (previously added) entity
	 * 
	 * @return null
	 */
	@UiAction
	public String cancel() {
		if (getEditMode() != null && getEditMode() == false && getCurrent() != null)
			deleteCurrent();
		return null;
	}

	public Boolean getEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public long getCurrentId() {
		return currentId;
	}

    @UiCached
    public String getEditTitle() {
        if (getEditMode() != null && getEditMode()) {
            return getActionTitle("edit", "ui.page.title.edit");
        } else {
            return getActionTitle("add", "ui.page.title.add");
        }
    }

	@Override
	public boolean canEdit(T entity) {
		return entity != null && hasOption(EntityControllerOption.EDIT);
	}

	@Override
	public boolean canDelete(T entity) {
		return entity != null && hasOption(EntityControllerOption.DELETE);
	}

	protected List<T> getAdded() {
		return added;
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (NULL_KEY.equals(value)) {
			return null;
		}

		Long id = getIdConverter().toObject(value);
		for(T add : added) {
			if(add.getId() == id)
				return add;
		}
		return findEntity(id);
	}
}

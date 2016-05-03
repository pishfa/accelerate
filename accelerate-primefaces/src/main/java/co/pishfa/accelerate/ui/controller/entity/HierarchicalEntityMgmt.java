package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.cache.UiCached;
import co.pishfa.accelerate.entity.hierarchical.HierarchicalEntity;
import co.pishfa.accelerate.ui.UiAction;
import co.pishfa.accelerate.ui.UiMessage;
import co.pishfa.accelerate.ui.controller.ViewController;
import co.pishfa.accelerate.ui.param.RequiredParams;
import co.pishfa.security.entity.authorization.SecuredEntity;
import org.apache.commons.lang3.Validate;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.util.List;

/**
 * Can be used to manage a tree of hierarchical entities.
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public class HierarchicalEntityMgmt<T extends HierarchicalEntity<T,K>, K> extends HierarchicalEntityView<T,K> {

    private static final String ID_PARAM_NAME = "id";
    public static final String NULL_KEY = "null";

    private Boolean editMode;

    private TreeNode prevNode;

    public HierarchicalEntityMgmt(Class<T> entityClass, Class<K> keyClass) {
        super(entityClass, keyClass);
    }

    public HierarchicalEntityMgmt() {
        super();
    }

    @Override
    protected void setDefaultOptions() {
        super.setDefaultOptions();
        setOption(EntityControllerOption.ADD, true);
        setOption(EntityControllerOption.EDIT, true);
        setOption(EntityControllerOption.DELETE, true);
        setOption(EntityControllerOption.ID, ID_PARAM_NAME);
        setOption(EntityControllerOption.PRESERVE_SELECTED, true);
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

    /**
     * Reloads current based on its current id.
     */
    protected void reloadCurrent() {
        loadCurrent(getCurrent().getId());
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
            prevNode = getCurrentNode();
            newCurrent();
            checkAddPermission(getCurrent());
            addEdit();
        }
        return null;
    }

    /**
     * Creates a new entity (which is obtained by calling {@link #newEntity()}) and sets the current to it.
     * The parent of this new entity is set according to the value of current.
     */
    protected void newCurrent() {
        T entity = newEntity();
        entity.setParent(getCurrent());
        setCurrent(entity);
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
            prevNode = getCurrentNode();
            addEdit();
        }
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
     * Saves the current entity. By default, it saves the entity by calling {@link #applyCurrent()}.
     *
     * @return null
     */
    @UiAction
    @UiMessage
    public String save() {
        applyCurrent();
        return null;
    }

    /**
     *
     * @return null
     */
    @UiAction
    @UiMessage
    public String apply() {
        applyCurrent();
        return null;
    }

    /**
     * Saves the current entity, and sets the current to the newly saved entity
     */
    @SuppressWarnings("unchecked")
    protected void applyCurrent() {
        if (getChildControllers() != null) {
            for (ViewController controller : getChildControllers()) {
                if (controller instanceof EntityChildMgmt) {
                    ((EntityChildMgmt) controller).preCommit();
                }
            }
        }

        if(!isEditMode()) {
            T parent = null;
            if(getCurrentNode() != null) {
                T currentNodeData = getData(getCurrentNode());
                if(currentNodeData != null)
                    parent = getEntityService().findById(currentNodeData.getId());
                expandNode(getCurrentNode());
            }
            if(parent != null) {
                getEntityService().addChild(parent, getCurrent());
            } else
                getEntityService().setParent(null, getCurrent(), true);
            setCurrent(saveEntity(getCurrent())); //assume cascading to parent
            if(getCurrentNode() != null) { //propagate possible changes to parent (such as leaf)
                setData(getCurrentNode(), getCurrent().getParent());
            }
            setCurrentNode(addNode(getCurrent(), getCurrent().getParent(), getCurrentNode(), true, null));
        } else {
            setCurrent(saveEntity(getCurrent()));
            setData(getCurrentNode(), getCurrent());
        }

        prevNode = getCurrentNode();
        if (getChildControllers() != null) {
            for (ViewController controller : getChildControllers()) {
                if (controller instanceof EntityChildMgmt) {
                    ((EntityChildMgmt) controller).commit(getCurrent());
                }
            }
        }
    }

    protected void setData(TreeNode node, T data) {
        ((DefaultTreeNode) node).setData(data);
    }

    protected T getData(TreeNode node) {
        return node == null? null : (T) node.getData();
    }

    /**
     * Deletes the current entity. It checks
     * for delete permission and then calls {@link #deleteEntity(co.pishfa.accelerate.entity.common.Entity)}.
     *
     * @return null
     */
    @UiAction
    @UiMessage
    public String delete() {
        if (hasOption(EntityControllerOption.DELETE)) {
            loadCurrent();
            checkDeletePermission(getCurrent());
            T parent = getCurrent().getParent();
            if(parent != null) {
                getEntityService().removeChild(parent, getCurrent());
            }
            deleteEntity(getCurrent());
            TreeNode parentNode = getCurrentNode().getParent();
            if(parentNode != null) {
                parentNode.getChildren().remove(getCurrentNode());
                if(parent != null) {
                    setData(parentNode, getEntityService().edit(parent));
                }
            }
            prevNode = parentNode;
            setCurrentNode(parentNode);
        }
        return null;
    }

    /**
     * Deletes the given entity by first checking for security permission by calling {@link #checkDeletePermission(co.pishfa.accelerate.entity.common.Entity)} and then calling {@link #deleteEntity(co.pishfa.accelerate.entity.common.Entity)}
     * .
     *
     * @return null
     */
    @UiAction
    @UiMessage
    public String delete(T entity) {
        Validate.notNull(entity, "Can not delete a null entity");
        if (hasOption(EntityControllerOption.DELETE)) {
            checkDeletePermission(entity);
            deleteEntity(entity);
            entityNodes.remove(entity);
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
        setCurrentNodes(null);
        setCurrentNode(hasOption(EntityControllerOption.PRESERVE_SELECTED)?prevNode:null);
        return null;
    }

    /**
     * If we are in add/edit mode reset this entity to its initial state otherwise reset the filter.
     */
    @UiAction
    public String reset() {
        if (getEditMode() != null) {
            if (getEditMode()) {
                reloadCurrent();
                addEdit();
            } else
                add();
            return null;
        } else
            return clean();
    }

    public String clean() {
        return null;
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
            return getActionTitle(getAction("edit"), "ui.page.title.edit");
        } else {
            return getActionTitle(getAction("add"), "ui.page.title.add");
        }
    }

    public void onDrop(TreeDragDropEvent event) {
        TreeNode dragNode = event.getDragNode();
        TreeNode dropNode = event.getDropNode();
        int dropIndex = event.getDropIndex();

        T source = getEntityService().findById(getData(dragNode).getId());
        T oldParent = source.getParent();
        T newParent = (T) dropNode.getData();
        if(newParent != null)
            newParent = getEntityService().findById(newParent.getId());

        if(oldParent != null && oldParent != newParent) {
            getEntityService().removeChild(oldParent, source);
            setData(dragNode.getParent(), getEntityService().edit(oldParent));
        }
        if(newParent == null || !newParent.isLeaf()) {
            //update rank using dropIndex
            updateRank(newParent, dropIndex, source);
        }

        if(newParent != null && newParent != oldParent) {
            getEntityService().addChild(newParent, source);
        } else {
            getEntityService().setParent(null, source, true);
        }
        source = getEntityService().edit(source); //assume the changes are propagated to parent too
        setData(dragNode, source);
        setCurrentNode(dragNode);
        setData(dropNode, source.getParent());
    }

    protected void updateRank(T newParent, int dropIndex, T source) {

    }

}

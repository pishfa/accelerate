package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.entity.hierarchical.HierarchicalEntity;
import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.persistence.hierarchical.HierarchicalEntityService;
import co.pishfa.accelerate.ui.UiAction;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.util.*;

/**
 * Can be used to display a tree of hierarchical entities.
 *
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public class HierarchicalEntityView<T extends HierarchicalEntity<T,K>, K> extends EntityController<T,K> {

    public static final String LOADING_TYPE = "loading";
    private DefaultTreeNode rootNode;
    private TreeNode currentNode;
    private TreeNode[] currentNodes;
    private T current;
    protected Map<T, DefaultTreeNode> entityNodes;

    public HierarchicalEntityView(Class<T> entityClass, Class<K> keyClass) {
        super(entityClass, keyClass);
    }

    public HierarchicalEntityView() {
        super();
    }

    @Override
    public HierarchicalEntityService<T,K> getEntityService() {
        return (HierarchicalEntityService) super.getEntityService();
    }

    public TreeNode getRootNode() {
        if(rootNode == null) {
            entityNodes = new HashMap<>();
            rootNode = createRootNode(); //required by primefaces (not shown)
            boolean local = hasOption(EntityControllerOption.LOCAL);
            if(!local) {
                List<T> roots = findData(null);
                if(roots != null)
                    for (T rootEntity : roots) {
                        addNode(rootEntity, null, rootNode, true, null);
                    }
            } else {
                List<T> list = findData(0);
                if(list != null)
                    addList(0, list, rootNode);
            }
        }
        return rootNode;
    }

    protected DefaultTreeNode createRootNode() {
        return addNode(null, null, null, false, null);
    }

    protected List<T> findData(Integer levels) {
        if(levels == null)
            return getEntityService().findRoots(getFilter());
        else
            return getEntityService().findOrderByDepth(levels, getFilter());
    }

    private void addList(int levels, List<T> list, DefaultTreeNode rootNode) {
        for(T entity : list) {
            T parent = entity.getParent();
            DefaultTreeNode parentNode = parent == null? rootNode : entityNodes.get(parent);
            addNode(entity, parent, parentNode, entity.getDepth() + 1 == levels, null);
        }
    }

    public void setRootNode(DefaultTreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public Object onNodeExpand(NodeExpandEvent event) {
        TreeNode node = event.getTreeNode();
        expandNode((DefaultTreeNode) node);
        return null;
    }

    protected void expandNode(TreeNode node) {
        if(node != null && node.getData() != null && !((T)node.getData()).isLeaf()) {
            clearLazyState(node);
            node.setExpanded(true);
            if (node.getChildCount() == 0) {
                T entity = (T) node.getData();
                List<T> children = getEntityService().findDirectChildren(entity, getFilter());
                for (T child : children) {
                    TreeNode childNode = addNode(child, entity, node, true, null);
                }
            }
        }
    }

    protected void clearLazyState(TreeNode node) {
        if(node.getChildCount() == 1 && node.getChildren().get(0).getType().equals(LOADING_TYPE)) {
            node.getChildren().clear();
        }
    }

    protected DefaultTreeNode addNode(T entity, T parent, TreeNode parentNode, boolean addLazy, String type) {
        DefaultTreeNode node = null;
        if(type != null)
            node = new DefaultTreeNode(type, entity, parentNode);
        else
            node = new DefaultTreeNode(entity, parentNode);

        if(addLazy && entity != null && !entity.isLeaf()) {
            new DefaultTreeNode(LOADING_TYPE, null, node);
        } else {
            node.setExpanded(true);
        }
        if(entity != null)
            entityNodes.put(entity, node);
        return node;
    }

    protected Filter getFilter() {
        return null;
    }

    @UiAction
    @Override
    public String load() {
        setRootNode(null);
        setCurrentNode(null);
        setCurrent(null);
        setCurrentNodes(null);
        return super.load();
    }

    /**
     * This method unifies single and multiple selection models by first look at the {@link #getCurrent()} and if it is
     * null look at {@link #getCurrentNodes()} ()}.
     *
     * @return all elements that are selected. Empty list if no element is selected.
     */
    public List<T> getSelected() {
        if (getCurrent() != null) {
            return Collections.singletonList(getCurrent());
        } else if (getCurrentNodes() != null) {
            List<T> res = new ArrayList<>(getCurrentNodes().length);
            for(TreeNode node : getCurrentNodes()) {
                res.add((T) node.getData());
            }
            return res;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Makes the given list as selected.
     * TODO: only works when all the nodes are already loaded
     */
    public void setSelected(List<T> list) {
        getRootNode();
        List<DefaultTreeNode> nodesList = new ArrayList<>();
        for(T entity : list) {
            DefaultTreeNode node = entityNodes.get(entity);
            node.setSelected(true);
            nodesList.add(node);
        }
        currentNodes = nodesList.toArray(new TreeNode[]{});
    }

    public void setSelected(T entity) {
        if(entity == null) {
            setCurrentNode(null);
            return;
        }
        getRootNode();
        List<DefaultTreeNode> nodesList = new ArrayList<>();
        DefaultTreeNode node = entityNodes.get(entity);
        node.setSelected(true);
        nodesList.add(node);
        currentNode = node;
        currentNodes = nodesList.toArray(new TreeNode[]{});
    }

    public T getCurrent() {
        return current;
    }

    public void setCurrent(T current) {
        this.current = current;
    }

    public TreeNode[] getCurrentNodes() {
        return currentNodes;
    }

    public void setCurrentNodes(TreeNode[] currents) {
        this.currentNodes = currents;
    }

    public TreeNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(TreeNode currentNode) {
        if(this.currentNode != null) {
            this.currentNode.setSelected(false);
        }
        this.currentNode = currentNode;
        if(currentNode == null)
            setCurrent(null);
        else {
            currentNode.setSelected(true);
            setCurrent((T) currentNode.getData());
        }
    }

    //Source: http://forum.primefaces.org/viewtopic.php?f=3&t=204&sid=dd0db716b0f62c696b0657a246f08147&start=10
    /**
     * Expands all nodes in the tree
     */
    public void expandAll(){
        expandAllNodes(rootNode, true);
    }

    /**
     * Collapse all nodes in the tree
     */
    public void collapseAll(){
        expandAllNodes(rootNode, false);
    }

    /**
     * Expands all nodes in the tree if expand is true. Otherwise, collapses all
     * nodes in the tree.
     *
     * @param treeNode
     * which should be expanded or collapsed
     * @param expand
     * true if all nodes should be expanded. Otherwise false
     */
    public void expandAllNodes(TreeNode treeNode, boolean expand) {
        List<TreeNode> children = treeNode.getChildren();
        for (TreeNode child : children) {
            expandAllNodes(child, expand);
        }
        treeNode.setExpanded(expand);
    }

}

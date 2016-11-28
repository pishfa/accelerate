package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.ui.UiAction;
import co.pishfa.accelerate.ui.controller.UiController;
import org.apache.commons.lang3.Validate;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.util.Collections;
import java.util.List;

/**
 * A controller that keeps a list of entities and those entities that are selected. It can also act as a
 * {@link Converter} for these entities. There is no security filtering but only its view action is checked. This
 * security checking is done only if the {@link UiController} annotation has the viewId properly setuped. If it has
 * local option, the conversion is done without referring to the entity service. This controller has current and currents
 * fields to support selection scenarios.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 * @param <T>
 *            type of entities of this controller
 * 
 */
public class EntityList<T extends Entity<K>, K> extends EntityController<T,K> implements Converter {

	private static final long serialVersionUID = 1L;
	public static final String NULL_KEY = "null";

	private List<T> data;
	private T current;
	private List<T> currents;

	public EntityList(Class<T> entityClass, Class<K> keyClass) {
		super(entityClass, keyClass);
	}

	public EntityList() {
		super();
	}

	@Override
	public String load() {
        setData(null);
		setCurrents(null);
        setCurrent(null);
		return null;
	}

	/**
	 * This method unifies single and multiple selection models by look at the {@link #getCurrent()} if MULTI_SELECT option is false
     * otherwise look at {@link #getCurrents()}.
	 * 
	 * @return all elements that are selected. Empty list if no element is selected.
	 */
	public List<T> getSelected() {
		if (!hasOption(EntityControllerOption.MULTI_SELECT)) {
			return getCurrent() == null ? Collections.EMPTY_LIST : Collections.singletonList(getCurrent());
		} else if (getCurrents() != null) {
			return getCurrents();
		} else {
			return Collections.emptyList();
		}
	}

    /**
     *
     * @return the number of selected items equals to {@link #getSelected()}.size() but more efficiently
     */
    public int getSelectedSize() {
        if (!hasOption(EntityControllerOption.MULTI_SELECT)) {
            return getCurrent() == null ? 0 : 1;
        } else if (getCurrents() != null) {
            return getCurrents().size();
        } else {
            return 0;
        }
    }

	/**
	 * Provides the model data to be filled. This method is called when {@link #load()} decides to fill the model. By
	 * default this method queries the underlying service and finds the associated data according to the current filter
	 * returned by {@link #getFilter()}
	 */
	protected List<T> findData() {
		return getEntityService().find(getFilter());
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	/**
	 * 
	 * @return the data associated with this model. Loads the data if necessary.
	 */
	public List<T> getData() {
		if (data == null) {
			setData(findData());
		}
		return data;
	}

	public int getCount() {
		return getData().size();
	}

	protected Filter<T> getFilter() {
		return null;
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return getValue(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return getKey((T) value);
	}

	/**
	 * Finds the entity with the given id. In Local mode, the search is done locally through the objects returned by
	 * {@link #getData()}.
	 */
	@Override
	protected T findEntity(K id) {
		if (hasOption(EntityControllerOption.LOCAL)) {
			for (T entity : getData()) {
				if (entity.getId().equals(id)) {
					return entity;
				}
			}
			return null;
		} else {
			return super.findEntity(id);
		}
	}

	public String getKey(T value) {
		return value == null ? NULL_KEY : getIdConverter().toString(value.getId());
	}

	public T getValue(String key) {
        Validate.notNull(key);

		try {
			if (NULL_KEY.equals(key)) {
				return null;
			}

			return findEntity(getIdConverter().toObject(key));
		} catch (Exception e) {
			getLogger().error("", e);
		}
		return null;
	}

	public T getCurrent() {
		return current;
	}

	public void setCurrent(T current) {
		this.current = current;
	}

	public List<T> getCurrents() {
		return currents;
	}

	public void setCurrents(List<T> currents) {
		this.currents = currents;
		if(currents == null || currents.size() != 1)
			setCurrent(null);
		else
			setCurrent(currents.get(0));
	}

	public boolean hasPagination() {
		return false;
	}

	@UiAction
	public String show() {
		return load();
	}

	/**
	 * Selects the selected items. This action can be used in selection components. By default, this method iterates
	 * through the selected items and for each one call the {@link #select(Entity)} method.
	 *
	 * @return null
	 */
	@UiAction
	public String select() {
		for (T e : getSelected()) {
			select(e);
		}
		return null;
	}

	/**
	 * This method is called by {@link #select()}. You can implement your logic for selection components.
	 */
	public void select(T e) {
	}

    @UiAction
    public String cancel() {
        return null;
    }

}

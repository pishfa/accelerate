package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.ui.controller.ViewController;
import co.pishfa.accelerate.ui.param.RequiredParams;
import co.pishfa.accelerate.ui.phase.PhaseId;
import co.pishfa.accelerate.ui.phase.UiPhaseAction;

/**
 * This controller is suitable for pages that want to display the details of one entity. It loads the entity based on
 * the passed id parameter to the current.
 * <p>
 * This controller checks the view permission by calling {@link #checkViewPermission(co.pishfa.accelerate.entity.common.Entity)} on load. By default it checks whether user
 * has {@link #getViewAction()}.</p>
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class EntityDisplay<T extends Entity<K>, K> extends EntityController<T,K> {

	private static final long serialVersionUID = 1L;

    private static final String ID_PARAM_NAME = "id";

	private T current;

	public EntityDisplay(Class<T> entityClass, Class<K> keyClass) {
		super(entityClass, keyClass);
	}

	public EntityDisplay() {
	}

    @Override
    protected void setDefaultOptions() {
        super.setDefaultOptions();
        setOption(EntityControllerOption.ID, ID_PARAM_NAME);
    }

	@Override
	public String load() {
		loadCurrent();
		checkViewPermission(getCurrent());
		return null;
	}

	/**
	 * loads the current using the id param.
	 */
	protected void loadCurrent() {
		loadCurrent(getIdParam());
	}

	protected void loadCurrent(K id) {
		setCurrent(findEntity(id));
	}

	protected K getIdParam() {
		return getIdConverter().toObject(RequiredParams.getString(getIdParamName()));
	}

	protected String getIdParamName() {
		return (String) getOption(EntityControllerOption.ID);
	}

    public boolean reload() {
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

	public T getCurrent() {
		return current;
	}

	@SuppressWarnings("unchecked")
	public void setCurrent(T current) {
		this.current = current;
		if (getChildControllers() != null) {
			for (ViewController controller : getChildControllers()) {
				if (controller instanceof EntityChildMgmt) {
					((EntityChildMgmt) controller).setParent(current);
				}
			}
		}
	}

}

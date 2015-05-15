package co.pishfa.accelerate.ui.component;

import co.pishfa.accelerate.i18n.domain.Locale;
import co.pishfa.accelerate.persistence.filter.DateInterval;
import com.ibm.icu.util.Calendar;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@FacesComponent("dateInterval")
public class InputDateInterval extends UIInput implements NamingContainer {

	private UIInput select;

	/**
	 * Returns the component family of {@link javax.faces.component.UINamingContainer}. (that's just required by composite component)
	 */
	@Override
	public String getFamily() {
		return UINamingContainer.COMPONENT_FAMILY;
	}

    @Override
    public Object getSubmittedValue() {
        return select.getValue();
    }

    @Override
    protected Object getConvertedValue(FacesContext context, Object newSubmittedValue) throws ConverterException {
        if(newSubmittedValue == null || newSubmittedValue.equals("null"))
            return null;
        int option = Integer.parseInt(String.valueOf(newSubmittedValue));
        Calendar calendar = Locale.getInstance().getCalendar();
        DateInterval res = new DateInterval();
        switch (option) {
            case 0:
                return null;
            case 1:
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                res.setStart(calendar.getTime());
                calendar.set(Calendar.HOUR_OF_DAY, 24);
                res.setEnd(calendar.getTime());
                break;
            case 2:
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                res.setStart(calendar.getTime());
                calendar.set(Calendar.HOUR_OF_DAY, 24);
                res.setEnd(calendar.getTime());
                break;
            case 3:
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                res.setStart(calendar.getTime());
                break;
            case 4:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                res.setStart(calendar.getTime());
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                res.setEnd(calendar.getTime());
                break;
            case 5:
                calendar.set(Calendar.MONTH, -1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                res.setStart(calendar.getTime());
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                res.setEnd(calendar.getTime());
                break;
            case 6:
                calendar.set(Calendar.DAY_OF_MONTH, -30);
                res.setStart(calendar.getTime());
                break;
            case 7:
                calendar.set(Calendar.MONTH, 0);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                res.setStart(calendar.getTime());
                break;
            case 8:
                calendar.set(Calendar.MONTH, 0);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                res.setEnd(calendar.getTime());
                calendar.set(Calendar.YEAR, -1);
                res.setStart(calendar.getTime());
                break;
            case 9:
                calendar.set(Calendar.YEAR, -1);
                res.setStart(calendar.getTime());
                break;
        }
        return res;
    }

    /**
	 * Return specified attribute value or otherwise the specified default if it's null.
	 */
	@SuppressWarnings("unchecked")
	private <T> T getAttributeValue(String key, T defaultValue) {
		T value = (T) getAttributes().get(key);
		return (value != null) ? value : defaultValue;
	}

    public UIInput getSelect() {
        return select;
    }

    public void setSelect(UIInput select) {
        this.select = select;
    }
}
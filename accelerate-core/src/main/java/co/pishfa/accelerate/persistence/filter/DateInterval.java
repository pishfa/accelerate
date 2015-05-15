package co.pishfa.accelerate.persistence.filter;

import java.util.Date;

/**
 * Specifies an interval between two dates for filtering purposes.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class DateInterval implements FilterInterval {

	private Date start;
	private Date end;

    public DateInterval() {
    }

    public DateInterval(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	@Override
	public Object getIntervalStart() {
		return getStart();
	}

	@Override
	public Object getIntervalEnd() {
		return getEnd();
	}

}

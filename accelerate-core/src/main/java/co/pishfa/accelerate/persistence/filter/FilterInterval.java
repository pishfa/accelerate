package co.pishfa.accelerate.persistence.filter;

/**
 * Specifies an interval to be used in filtering.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public interface FilterInterval {

	/**
	 * 
	 * @return the start of interval. Null means open start interval.
	 */
	Object getIntervalStart();

	/**
	 * 
	 * @return the end of interval. Null means open ended interval.
	 */
	Object getIntervalEnd();

}

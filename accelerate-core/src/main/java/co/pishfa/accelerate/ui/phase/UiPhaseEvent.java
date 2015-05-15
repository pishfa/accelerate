package co.pishfa.accelerate.ui.phase;

import javax.faces.event.PhaseEvent;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class UiPhaseEvent {

	private final PhaseEvent event;
	private final boolean after;

	public UiPhaseEvent(PhaseEvent event, boolean after) {
		super();
		this.event = event;
		this.after = after;
	}

	public PhaseEvent getEvent() {
		return event;
	}

	public boolean isAfter() {
		return after;
	}

}

package co.pishfa.accelerate.ui.phase;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import co.pishfa.accelerate.cdi.CdiUtils;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class PhaseListenerCdiBridge implements PhaseListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void afterPhase(PhaseEvent event) {
		notify(event, true);
	}

	protected void notify(PhaseEvent event, boolean after) {
		CdiUtils.getBeanManager().fireEvent(new UiPhaseEvent(event, after));
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		notify(event, false);
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}

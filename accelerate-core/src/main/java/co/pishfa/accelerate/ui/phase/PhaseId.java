package co.pishfa.accelerate.ui.phase;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public enum PhaseId {
	ANY_PHASE,
	RESTORE_VIEW,
	APPLY_REQUEST_VALUES,
	PROCESS_VALIDATIONS,
	UPDATE_MODEL_VALUES,
	INVOKE_APPLICATION,
	RENDER_RESPONSE;

	public boolean equals(javax.faces.event.PhaseId phaseId) {
		return ordinal() == phaseId.getOrdinal();
	}
}

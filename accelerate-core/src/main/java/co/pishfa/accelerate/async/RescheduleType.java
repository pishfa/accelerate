package co.pishfa.accelerate.async;

/**
 * Specifies if a job with the same name exists, how scheduler behaves.
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public enum RescheduleType {

    /**
     * Delete the previous job, and create a new one
     */
    DELETE_PREV,
    /**
     * Skip creating of new job
     */
    SKIP,
    /**
     * Create a new job regardless of previous job
     */
    NEW;
}

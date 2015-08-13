package situationHandling.exceptions;

import situationHandling.storage.datatypes.Action;

/**
 * The InvalidActionException can be thrown when an invalid action is used. An
 * action might be invalid, when required fields are omitted or the action is
 * semantically invalid.
 * 
 * @see Action
 */
public class InvalidActionException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2218182694949963434L;

    /**
     * Instantiates a new invalid action exception.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public InvalidActionException(String message, Throwable cause) {
	super(message, cause);
    }

    /**
     * Instantiates a new invalid action exception.
     *
     * @param message
     *            the message
     */
    public InvalidActionException(String message) {
	super(message);
    }

}

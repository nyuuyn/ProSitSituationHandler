package situationHandling.exceptions;

import situationHandling.storage.datatypes.Rule;

/**
 * The InvalidRuleException can be thrown when an invalid rule is used. An rule
 * might be invalid, when required fields are omitted or the rule is
 * semantically invalid.
 *
 * @see Rule
 */
public class InvalidRuleException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4233475775108797849L;

	/**
	 * Instantiates a new invalid rule exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public InvalidRuleException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new invalid rule exception.
	 *
	 * @param message
	 *            the message
	 */
	public InvalidRuleException(String message) {
		super(message);
	}

}

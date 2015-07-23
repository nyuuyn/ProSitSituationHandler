/**
 * 
 */
package situationHandling.exceptions;

import situationHandling.storage.datatypes.Endpoint;


/**
 * The InvalidEndpointException can be thrown when an invalid endpoint is used. An
 * endpoint might be invalid, when required fields are omitted or the endpoint is
 * semantically invalid.
 *
 *@see Endpoint
 *
 * @author Stefan
 */
public class InvalidEndpointException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 498281200904935130L;

	/**
	 * Instantiates a new invalid endpoint exception.
	 *
	 * @param message the message
	 */
	public InvalidEndpointException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new invalid endpoint exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidEndpointException(String message, Throwable cause) {
		super(message, cause);
	}

	
}

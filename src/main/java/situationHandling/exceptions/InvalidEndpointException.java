/**
 * 
 */
package situationHandling.exceptions;

/**
 * @author Stefan
 *
 */
public class InvalidEndpointException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 498281200904935130L;

	/**
	 * @param message
	 */
	public InvalidEndpointException(String message) {
		super(message);
	}

	public InvalidEndpointException(String message, Throwable cause) {
		super(message, cause);
	}

	
}

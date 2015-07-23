package situationHandling.exceptions;

public class InvalidActionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2218182694949963434L;

	public InvalidActionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidActionException(String message) {
		super(message);
	}
	
	

}

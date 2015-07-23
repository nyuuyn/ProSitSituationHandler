package situationHandling.exceptions;

public class InvalidRuleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4233475775108797849L;

	public InvalidRuleException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidRuleException(String message) {
		super(message);
	}

	
}

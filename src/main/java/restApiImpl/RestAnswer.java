package restApiImpl;

/**
 * The Class RestAnswer is used to provide a appropriate answer for operations
 * of the Rest Api. An answer consists of a message that describes the result
 * and and answer code that associates the message with a certain object. The
 * answer code is for example an id. <br>
 * {@code RestAnswer} is only used as answer for successful operations. For
 * errors, an appropriate HTTP-Response code plus plain text is used.
 */
public class RestAnswer {

	/** The message that describes the result. */
	private String message;

	/** The answer code that associates the message with a certain object */
	private String answerCode;

	/**
	 * Instantiates a new rest answer. Use the setters to initiate the object.
	 */
	public RestAnswer() {
	}

	/**
	 * Instantiates a new rest answer and set the fields.
	 *
	 * @param message
	 *            the message  that describes the result.
	 * @param answerCode
	 *            the answer code that associates the message with a certain object
	 */
	public RestAnswer(String message, String answerCode) {
		super();
		this.message = message;
		this.answerCode = answerCode;
	}

	/**
	 * Gets the message that describes the result.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message that describes the result.
	 *
	 * @param message
	 *            the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the answer code that associates the message with a certain object.
	 *
	 * @return the answer code
	 */
	public String getAnswerCode() {
		return answerCode;
	}

	/**
	 * Sets the answer code that associates the message with a certain object.
	 *
	 * @param answerCode
	 *            the new answer code
	 */
	public void setAnswerCode(String answerCode) {
		this.answerCode = answerCode;
	}

}

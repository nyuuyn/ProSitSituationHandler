package situationHandling.workflowOperations;

/**
 * 
 * The Class DecisionAnswer represents the answer for a decision request.
 * <p>
 * A decision request is sent to a user, when the situation handler could was
 * not able to find a unique endpoint to handle a workflow request and when the
 * workflow request specified a user to make the decision.
 * <p>
 * The answer contains the id of the request it relates to and the coice that
 * the user made.
 * 
 * @author Stefan
 *
 */
public class DecisionAnswer {

    /**
     * The id of the decision request.
     */
    private String requestId;

    /**
     * The choice the user made, i.e. the id of the endpoint the user selected.
     */
    private String choice;

    /**
     * Creates a new instance of Decision Answer.
     */
    public DecisionAnswer() {
    }

    /**
     * Creates a new instance of DecisionAnswer.
     * 
     * 
     * @param requestId
     *            The id of the decision request.
     * @param choice
     *            The choice the user made, i.e. the id of the endpoint the user
     *            selected.
     */
    DecisionAnswer(String requestId, String choice) {
	this.requestId = requestId;
	this.choice = choice;
    }

    /**
     * 
     * Sets the id of the decision request.
     * 
     * @param requestId
     *            the requestId to set
     */
    public void setRequestId(String requestId) {
	this.requestId = requestId;
    }

    /**
     * 
     * Sets the choice the user made, i.e. the id of the endpoint the user
     * selected.
     * 
     * @param choice
     *            the choice to set
     */
    public void setChoice(String choice) {
	this.choice = choice;
    }

    /**
     * Gets the id of the decision request.
     * 
     * @return the requestId
     */
    public String getRequestId() {
	return requestId;
    }

    /**
     * Gets the choice the user made, i.e. the id of the endpoint the user
     * selected.
     * 
     * @return the choice
     */
    public String getChoice() {
	return choice;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "DecisionAnswer [requestId=" + requestId + ", choice=" + choice + "]";
    }

}

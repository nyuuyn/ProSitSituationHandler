package situationHandling.workflowOperations;

//TODO
public class DecisionAnswer {

    private String requestId;
    private String choice;
    
    

    /**
     * 
     */
    public DecisionAnswer() {
    }

    /**
     * @param requestId
     * @param choice
     */
    DecisionAnswer(String requestId, String choice) {
	this.requestId = requestId;
	this.choice = choice;
    }
    
    

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * @param choice the choice to set
     */
    public void setChoice(String choice) {
        this.choice = choice;
    }

    /**
     * @return the requestId
     */
    public String getRequestId() {
	return requestId;
    }

    /**
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

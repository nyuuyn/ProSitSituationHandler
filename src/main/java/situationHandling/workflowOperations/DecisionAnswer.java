package situationHandling.workflowOperations;

//TODO
class DecisionAnswer {
    
    private String requestId;
    private String choice;
    
    
    
    /**
     * @param requestId
     * @param choice
     */
    DecisionAnswer(String requestId, String choice) {
	this.requestId = requestId;
	this.choice = choice;
    }
    /**
     * @return the requestId
     */
    String getRequestId() {
        return requestId;
    }
    /**
     * @return the choice
     */
    String getChoice() {
        return choice;
    }
    
    

}

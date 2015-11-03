package situationHandling.workflowOperations;

/**
 * This class represents the response the deployment service sends back to the
 * deployment-requester.
 * 
 * @author Stefan
 *
 */
public class DeployResponse {

    /**
     * The fragment that was deployed
     */
    private String fragmentName;

    /**
     * Describes the success of the deployment.
     */
    private boolean success;

    /**
     * The url of the newly created endpoint.
     */
    private String endpointUrl;

    /**
     * Creates a new instance
     */
    public DeployResponse() {
    }

    /**
     * Creates a new instance.
     * 
     * @param fragmentName
     *            The fragment that was deployed
     * @param endpointUrl
     *            The url of the newly created endpoint.
     * @param success
     *            Describes the success of the deployment.
     */
    public DeployResponse(String fragmentName, String endpointUrl, boolean success) {
	this.fragmentName = fragmentName;
	this.endpointUrl = endpointUrl;
	this.success = success;
    }

    /**
     * @return the fragmentName. The fragment that was deployed
     */
    public String getFragmentName() {
	return fragmentName;
    }

    /**
     * @param fragmentName
     *            the fragmentName to set. The fragment that was deployed
     */
    public void setFragmentName(String fragmentName) {
	this.fragmentName = fragmentName;
    }

    /**
     * @return the endpointUrl. The url of the newly created endpoint.
     */
    public String getEndpointUrl() {
	return endpointUrl;
    }

    /**
     * @param endpointUrl
     *            the endpointUrl to set. The url of the newly created endpoint.
     */
    public void setEndpointUrl(String endpointUrl) {
	this.endpointUrl = endpointUrl;
    }

    /**
     * @return the success Describes the success of the deployment.
     */
    public boolean isSuccess() {
	return success;
    }

    /**
     * @param success
     *            the success to set. Describes the success of the deployment.
     */
    public void setSuccess(boolean success) {
	this.success = success;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "DeployResponse [fragmentId=" + fragmentName + ", success=" + success
		+ ", endpointUrl=" + endpointUrl + "]";
    }

}

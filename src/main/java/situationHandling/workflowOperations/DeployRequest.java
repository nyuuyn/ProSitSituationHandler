package situationHandling.workflowOperations;

/**
 * Wrapper class for a request to deploy a bpel fragment.
 * 
 * @author Stefan
 *
 */
public class DeployRequest {

    /**
     * The name of the fragment.
     */
    private String fragmentName;

    /**
     * The url for callback.
     */
    private String callbackUrl;

    /**
     * Creates a new instance.
     */
    public DeployRequest() {
    }

    /**
     * Creates a new instance.
     * 
     * @param fragmentName
     *            The name of the fragment.
     * @param callbackUrl
     */
    public DeployRequest(String fragmentName, String callbackUrl) {
	this.fragmentName = fragmentName;
	this.callbackUrl = callbackUrl;
    }

    /**
     * @return The name of the fragment.
     */
    public String getFragmentName() {
	return fragmentName;
    }

    /**
     * @param fragmentName
     *            the fragmentname to set
     */
    public void setFragmentName(String fragmentName) {
	this.fragmentName = fragmentName;
    }

    /**
     * @return the callbackUrl
     */
    public String getCallbackUrl() {
	return callbackUrl;
    }

    /**
     * @param callbackUrl
     *            the callbackUrl to set
     */
    public void setCallbackUrl(String callbackUrl) {
	this.callbackUrl = callbackUrl;
    }

}

package situationHandling.storage;

// TODO: Auto-generated Javadoc
/**
 * 
 * Factory for accessing the different storages. Gives access to the Endpoint
 * Storage and to the Rule Storage.
 * 
 * 
 * @author Stefan
 *
 */
public class StorageAccessFactory {

	/**
	 * Gets the endpoint storage access.
	 *
	 * @return the endpoint storage access
	 */
	public static EndpointStorageAccess getEndpointStorageAccess() {
		return new EndpointStorageAccessImpl();
	}

	/**
	 * Ge rule storage access.
	 *
	 * @return the rule storage access
	 */
	public static RuleStorageAccess geRuleStorageAccess() {
		return new RuleStorageAccessImpl();
	}

}

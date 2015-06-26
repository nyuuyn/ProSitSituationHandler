package situationHandling.storage;

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

	public static EndpointStorageAccess getEndpointStorageAccess() {
		return new EndpointStorageAccessImpl();
	}

	public static RuleStorageAccess geRuleStorageAccess() {
		return new RuleStorageAccessImpl();
	}

}

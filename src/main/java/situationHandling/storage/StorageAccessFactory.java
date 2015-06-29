package situationHandling.storage;

/**
 * 
 * The class StorageAccessFactory is a Factory that creates instances of the
 * classes that implement the interfaces for accessing the storages. The
 * Interfaces give access to the Endpoint Storage and to the Rule Storage.
 * <p>
 * Using this class is the only way to create instances of the implementing
 * classes, so its usage is required.
 * 
 * @author Stefan
 *
 */
public class StorageAccessFactory {

	/**
	 * Gets an instance of {@link EndpointStorageAccess} to access the endpoint
	 * storage.
	 *
	 * @return an instance of {@link EndpointStorageAccess}
	 */
	public static EndpointStorageAccess getEndpointStorageAccess() {
		return new EndpointStorageAccessImpl();
	}

	/**
	 * Gets an instance of {@link RuleStorageAccess} to access the rule
	 * storage.
	 *
	 * @return an instance of {@link RuleStorageAccess}
	 */
	public static RuleStorageAccess getRuleStorageAccess() {
		return new RuleStorageAccessImpl();
	}

}

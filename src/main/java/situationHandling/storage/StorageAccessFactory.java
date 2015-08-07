package situationHandling.storage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	 * Session for accessing hibernate
	 */
	private static HibernateSession hibernateSession = new HibernateSession();

	//TODO: Man koennte das Theading auch über Camel machen. Dazu ueber den Context Pools erstellen.
	private static ExecutorService threadExecutor = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	

	/**
	 * Gets an instance of {@link EndpointStorageAccess} to access the endpoint
	 * storage.
	 *
	 * @return an instance of {@link EndpointStorageAccess}
	 */
	public static EndpointStorageAccess getEndpointStorageAccess() {
		return new EndpointStorageAccessWithSubscribe(
				new EndpointStorageAccessAdvancedChecks(
						hibernateSession.getSessionFactory()));

	}

	/**
	 * Gets an instance of {@link RuleStorageAccess} to access the rule storage.
	 *
	 * @return an instance of {@link RuleStorageAccess}
	 */
	public static RuleStorageAccess getRuleStorageAccess() {
		return new RuleStorageAccessWithSubscribe(
				new RuleStorageAccessAdvancedChecks(
						hibernateSession.getSessionFactory()));
	}

	/**
	 * Gets an instance of {@link HistoryAccess} to access the history.
	 *
	 * @return an instance of {@link HistoryAccess}
	 */
	public static HistoryAccess getHistoryAccess() {
		return new HistoryAccess(hibernateSession.getSessionFactory(), threadExecutor);
	}

	/**
	 * Closes the access to the storage and releases all occupied ressources.
	 * Use this method before shutting down. The access cannot be reopened.
	 * 
	 */
	public static void closeStorageAccess() {
		hibernateSession.shutdown();
	}

}

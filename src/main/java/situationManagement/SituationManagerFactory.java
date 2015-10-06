package situationManagement;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

import main.SituationHandlerProperties;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.log4j.Logger;

import situationHandling.storage.datatypes.Situation;

/**
 * A factory for creating SituationManager objects. The Factory can also be used
 * to configure the situation management component. For example the cache can be
 * controlled via the factory.
 * <p>
 * The cache is enabled by default. To disable/enable the cache, use
 * {@code SituationManagerFactory#setCaching(boolean)}.
 */
public class SituationManagerFactory {

	/** The logger. */
	private static Logger logger = Logger
			.getLogger(SituationManagerFactory.class);

	/**
	 * The subscription handler to be used by the situation management. Stores
	 * the subscriptions, therefore all implementations of the situation manager
	 * should use the same subscription handler to avoid inconsistencies.
	 */
	private static SubscriptionHandler subscriptionHandler;



	/** The url of the srs (used for communication). */
	private static URL srsUrl;

	/**
	 * The situation cache. Uses LRU as replacement strategy. All
	 * implementations of the situation manager should use the same cache to
	 * avoid inconsistencies.
	 */
	private static Map<Situation, Boolean> situationCache = Collections
			.synchronizedMap(new LRUMap<Situation, Boolean>(50));

	/** Determines if the cache is enabled or not. */
	private static boolean cacheEnabled = true;

	static {
		try {
			/*
			 * Init the components managed by the factory.
			 */
			srsUrl = new URL(SituationHandlerProperties.SRS_ADDRESS);

			String ownIPAdress = InetAddress.getLocalHost().getHostAddress();

			URL ownAdress = new URL("http://" + ownIPAdress + ":"
					+ SituationHandlerProperties.NETWORK_PORT + "/"
					+ SituationHandlerProperties.SITUATION_ENDPOINT_PATH);

			subscriptionHandler = new SubscriptionHandler(ownAdress,
					new SRSCommunicator(srsUrl));
		} catch (MalformedURLException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets an instance of the situation manager. Usage of the cache is enabled
	 * by default. To disable/enable the cache, use
	 * {@code SituationManagerFactory#setCaching(boolean))}.
	 *
	 * @return the situation manager
	 */
	public static SituationManager getSituationManager() {
		if (cacheEnabled) {
			return new SituationManagerWithCache(subscriptionHandler,
					new SRSCommunicator(srsUrl), situationCache);
		} else {
			return new SituationManagerImpl(subscriptionHandler,
					new SRSCommunicator(srsUrl));
		}
	}

	/**
	 * Gets an instance of situation manager with an explicitly disabled cache.
	 * The queries will then alway go to the SRS and never to the cache. Ignores
	 * the setting made via {@code SituationManagerFactory#setCaching(boolean))}
	 * .
	 *
	 * @return the situation manager
	 */
	public static SituationManager getSituationManagerWithoutCache() {

		return new SituationManagerImpl(subscriptionHandler,
				new SRSCommunicator(srsUrl));

	}

	/**
	 * Enables or disables the cache.
	 *
	 * @param enabled
	 *            true to enable the cache, false to disable
	 */
	public static void setCaching(boolean enabled) {
		if (cacheEnabled == false && enabled == true) {
			// if cache is turned on, the cache is cleared to avoid the usage of
			// outdated mappings
			situationCache.clear();
			logger.debug("Situation cache cleared.");
		}
		cacheEnabled = enabled;
	}

	/**
	 * Sets the size of the cache, i.e. the maximum number of situations to be
	 * cached. When the cache is filled, the replacement candidate will be
	 * determined using the LRU-strategy.
	 *
	 * @param size
	 *            the new cache size
	 */
	public static void setCacheSize(int size) {
		situationCache = Collections
				.synchronizedMap(new LRUMap<Situation, Boolean>(size));
	}

	/**
	 * 
	 * @return the current size of the cache.
	 */
	public static int getCacheSize() {
		return situationCache.size();
	}

}

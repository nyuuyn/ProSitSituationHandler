package situationManagement;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.log4j.Logger;

import routes.GlobalProperties;
import situationHandling.storage.datatypes.Situation;

public class SituationManagerFactory {
	
	private static Logger logger = Logger.getLogger(SituationManagerFactory.class);

	private static SubscriptionHandler subscriptionHandler;
	private static URL srsUrl;
	private static Map<Situation, Boolean> situationCache = Collections
			.synchronizedMap(new LRUMap<Situation, Boolean>(50));

	private static boolean cacheEnabled = true;

	static {
		try {
			srsUrl = new URL("http://192.168.209.200:10010");

			String ownIPAdress = InetAddress.getLocalHost().getHostAddress();

			URL ownAdress = new URL(
					"http://" + ownIPAdress + ":" + GlobalProperties.NETWORK_PORT + "/SituationEndpoint");
			subscriptionHandler = new SubscriptionHandler(ownAdress, new SRSCommunicator(srsUrl));
		} catch (MalformedURLException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static SituationManager getSituationManager() {
		if (cacheEnabled) {
			return new SituationManagerWithCache(subscriptionHandler, new SRSCommunicator(srsUrl), situationCache);
		} else {
			return new SituationManagerImpl(subscriptionHandler, new SRSCommunicator(srsUrl));
		}
	}

	public static SituationManager getSituationManager(boolean caching) {
		if (caching) {
			return new SituationManagerWithCache(subscriptionHandler, new SRSCommunicator(srsUrl), situationCache);
		} else {
			return new SituationManagerImpl(subscriptionHandler, new SRSCommunicator(srsUrl));
		}
	}

	public static void setCaching(boolean enabled) {
		if (cacheEnabled == false && enabled == true) {
			//if cache is turned on, the cache is cleared to avoid the usage of outdated mappings
			situationCache.clear();
			logger.debug("Situation chace cleared.");
		}
		cacheEnabled = enabled;
	}

}

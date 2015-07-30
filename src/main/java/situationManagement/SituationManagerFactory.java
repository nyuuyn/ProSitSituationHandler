package situationManagement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;

import situationHandling.storage.datatypes.Situation;

public class SituationManagerFactory {

	private static SubscriptionHandler subscriptionHandler;
	private static URL srsUrl;
	private static Map<Situation, Boolean> situationCache =  Collections
			.synchronizedMap(new LRUMap<Situation, Boolean>(50));

	private static boolean cacheEnabled = true;

	static {
		try {
			srsUrl = new URL("http://192.168.209.200:10010");

			URL ownAdress = new URL("http://localhost:8081/SituationEndpoint");
			subscriptionHandler = new SubscriptionHandler(ownAdress,
					new SRSCommunicator(srsUrl));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static SituationManager getSituationManager() {
		if (cacheEnabled) {
			return new SituationManagerWithCache(subscriptionHandler,
					new SRSCommunicator(srsUrl), situationCache);
		} else {
			return new SituationManagerImpl(subscriptionHandler,
					new SRSCommunicator(srsUrl));
		}
	}

	public static SituationManager getSituationManager(boolean caching) {
		if (caching) {
			return new SituationManagerWithCache(subscriptionHandler,
					new SRSCommunicator(srsUrl), situationCache);
		} else {
			return new SituationManagerImpl(subscriptionHandler,
					new SRSCommunicator(srsUrl));
		}
	}

	public static void setCaching(boolean enabled) {
		cacheEnabled = enabled;
	}

}

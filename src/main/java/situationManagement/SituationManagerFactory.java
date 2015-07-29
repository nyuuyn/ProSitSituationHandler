package situationManagement;

import java.net.MalformedURLException;
import java.net.URL;

public class SituationManagerFactory {

	private static SubscriptionHandler subscriptionHandler;
	private static URL srsUrl;
	
	static {
		try {
			srsUrl = new URL("http://192.168.209.200:10010");

			URL ownAdress = new URL("http://localhost:8081/SituationEndpoint");
			subscriptionHandler = new SubscriptionHandler(ownAdress, new SRSCommunicator(srsUrl));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}


	public static SituationManager getSituationManager() {
		return new SituationManagerImpl(subscriptionHandler,
				new SRSCommunicator(srsUrl));
	}

}

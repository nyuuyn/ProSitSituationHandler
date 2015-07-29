package situationManagement;

import java.net.MalformedURLException;
import java.net.URL;

public class SituationManagerFactory {

	static {
		URL ownAdress;
		URL srsUrl;
		try {
//			srsUrl = new URL ("http://localhost:8888");
			srsUrl = new URL ("http://192.168.209.200:10010");
			srsCommunicator = new SRSCommunicator(srsUrl);
			
			ownAdress = new URL("http://localhost:8081/SituationEndpoint");
			subscriptionHandler = new SubscriptionHandler(ownAdress);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private static SubscriptionHandler subscriptionHandler;
	private static SRSCommunicator srsCommunicator;

	public static SituationManager getSituationManager() {
		return new SituationManagerImpl(subscriptionHandler, srsCommunicator);
	}

}

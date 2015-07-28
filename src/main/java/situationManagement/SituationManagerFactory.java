package situationManagement;

import java.net.MalformedURLException;
import java.net.URL;

public class SituationManagerFactory {

	static {
		URL ownAdress;
		try {
			ownAdress = new URL("http://localhost:8081/SituationEndpoint");
			subscriptionHandler = new SubscriptionHandler(ownAdress);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private static SubscriptionHandler subscriptionHandler;

	public static SituationManager getSituationManager() {
		return new SituationManagerImpl(subscriptionHandler);
	}

}

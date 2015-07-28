package situationManagement;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import situationHandling.storage.datatypes.Situation;

class SubscriptionHandler {
	
	private Map<Situation, Subscription> subscriptions = new HashMap<Situation, Subscription>();
	private URL ownAddress;

	SubscriptionHandler(URL ownAdress) {
		this.ownAddress = ownAddress;
	}

	public void subscribe(Situation situation) {

	}

	public void removeSubscription(Situation situation) {

	}

	// @Override
	// public boolean equals(Object object) {
	// if (object instanceof SubscriptionHandler){
	// SubscriptionHandler subscriptionHandler = (SubscriptionHandler) object;
	// //TODO: Equals Methode von situation
	// if (subscriptionHandler.situation.equals(this.situation)){
	// return true;
	// }
	// }
	// return false;
	//
	// }

}

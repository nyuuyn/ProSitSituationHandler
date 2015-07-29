package situationManagement;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import situationHandling.storage.datatypes.Situation;

class SubscriptionHandler {

	private Map<Situation, Subscription> subscriptions = new HashMap<Situation, Subscription>();
	private SRSCommunicator srsCommunicator;
	private URL ownAddress;

	SubscriptionHandler(URL ownAddress, SRSCommunicator srsCommunicator) {
		this.ownAddress = ownAddress;
		this.srsCommunicator = srsCommunicator;
	}

	public void subscribe(Situation situation) {
		if (subscriptions.containsKey(situation)) {
			subscriptions.get(situation).addSubscription();
		} else {
			subscriptions.put(situation, new Subscription());
			srsCommunicator.subscribe(situation, ownAddress);
		}

	}

	public void removeSubscription(Situation situation) {
		if (subscriptions.containsKey(situation)) {
			Subscription subscription = subscriptions.get(situation);
			subscription.removeSubsription();
			if (!subscription.subsriptionsAvailable()) {
				srsCommunicator.unsubscribe(situation, ownAddress);
				subscriptions.remove(situation);
			}
		}
	}

	public String getSubscriptionsAsString() {
		StringBuilder sb = new StringBuilder();
		sb.append("-----------Subscriptions:-----------");
		sb.append(System.getProperty("line.separator"));
		for (Situation situation : subscriptions.keySet()) {
			sb.append(situation.toString() + " --> "
					+ subscriptions.get(situation).toString());
			sb.append(System.getProperty("line.separator"));
		}
		sb.append("-----------End Subscriptions-----------");
		sb.append(System.getProperty("line.separator"));
		return sb.toString();
	}

}

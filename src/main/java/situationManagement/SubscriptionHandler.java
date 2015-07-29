package situationManagement;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import situationHandling.storage.EndpointStorageAccess;
import situationHandling.storage.RuleStorageAccess;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

class SubscriptionHandler {

	private Map<Situation, Subscription> subscriptions = new HashMap<Situation, Subscription>();
	private SRSCommunicator srsCommunicator;
	private URL ownAddress;

	SubscriptionHandler(URL ownAddress, SRSCommunicator srsCommunicator) {
		this.ownAddress = ownAddress;
		this.srsCommunicator = srsCommunicator;
	}

	void subscribe(Situation situation) {
		if (subscriptions.containsKey(situation)) {
			subscriptions.get(situation).addSubscription();
		} else {
			subscriptions.put(situation, new Subscription());
			srsCommunicator.subscribe(situation, ownAddress);
		}

	}

	void removeSubscription(Situation situation) {
		if (subscriptions.containsKey(situation)) {
			Subscription subscription = subscriptions.get(situation);
			subscription.removeSubsription();
			if (!subscription.subsriptionsAvailable()) {
				srsCommunicator.unsubscribe(situation, ownAddress);
				subscriptions.remove(situation);
			}
		}
	}

	void reloadSubscriptions() {
		subscriptions.clear();
		// do subscriptions for all rules
		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();
		for (Rule rule : rsa.getAllRules()) {
			subscribe(rule.getSituation());
		}

		// do subscriptions for all situations handled by endpoints
		EndpointStorageAccess esa = StorageAccessFactory
				.getEndpointStorageAccess();
		for (Endpoint endpoint : esa.getAllEndpoints()) {
			for (HandledSituation handledSituation : endpoint.getSituations()) {
				subscribe(new Situation(handledSituation.getSituationName(),
						handledSituation.getObjectName()));
			}
		}
	}

	String getSubscriptionsAsString() {
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

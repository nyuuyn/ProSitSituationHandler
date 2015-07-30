package situationManagement;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import situationHandling.storage.EndpointStorageAccess;
import situationHandling.storage.RuleStorageAccess;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

class SubscriptionHandler {

	private final static Logger logger = Logger
			.getLogger(SubscriptionHandler.class);

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
			logger.trace("Added additional subscription on " + situation);
			logger.trace(subscriptions.get(situation).toString());
		} else {
			logger.debug("Creating subscription on " + situation.toString());
			subscriptions.put(situation, new Subscription());
			srsCommunicator.subscribe(situation, ownAddress);
		}

	}

	void removeSubscription(Situation situation) {
		if (subscriptions.containsKey(situation)) {
			Subscription subscription = subscriptions.get(situation);
			subscription.removeSubsription();
			if (!subscription.subsriptionsAvailable()) {
				logger.debug("Deleting subscription on " + situation.toString());
				srsCommunicator.unsubscribe(situation, ownAddress);
				subscriptions.remove(situation);
			}else{
				logger.trace("Removed one subscription on " + situation);
				logger.trace(subscriptions.get(situation).toString());
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

		logger.debug("Subscriptions reloaded:\n" + getSubscriptionsAsString());
	}

	String getSubscriptionsAsString() {
		StringBuilder sb = new StringBuilder();
		sb.append("-----------Subscriptions:-----------");
		sb.append(System.getProperty("line.separator"));
		sb.append("Total Number of Subscriptions: " + subscriptions.size());
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

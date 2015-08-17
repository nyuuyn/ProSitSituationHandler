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

/**
 * The Class SubscriptionHandler can be used to manage the subscriptions on
 * situations. It the subscriptions to situation changes in an intelligent way,
 * i.e. there won't be double subscriptions and subscriptions are only deleted,
 * when they become obsolete.
 */
class SubscriptionHandler {

	/** The logger. */
	private final static Logger logger = Logger.getLogger(SubscriptionHandler.class);

	/** The subscriptions managed by this handler. */
	private Map<Situation, Subscription> subscriptions = new HashMap<Situation, Subscription>();

	/** The srs communicator to create/delete subscriptions. */
	private SRSCommunicator srsCommunicator;

	/**
	 * The address of the situation handler. The situation changes will be sent
	 * to this URL.
	 */
	private URL ownAddress;

	/**
	 * Instantiates a new subscription handler.
	 *
	 * @param ownAddress
	 *            The address of the situation handler. The situation changes
	 *            will be sent to this URL.
	 * @param srsCommunicator
	 *            the srs communicator to create/delete subscriptions.
	 */
	SubscriptionHandler(URL ownAddress, SRSCommunicator srsCommunicator) {
		this.ownAddress = ownAddress;
		this.srsCommunicator = srsCommunicator;
	}

	/**
	 * Subscribe to a situation. Only sends a subscription to the SRS for new
	 * subscriptions.
	 *
	 * @param situation
	 *            the situation to subscribe on.
	 */
	void subscribe(Situation situation) {
		if (subscriptions.containsKey(situation)) {
			subscriptions.get(situation).addSubscription();
			logger.trace("Added additional subscription on " + situation);
			logger.trace(subscriptions.get(situation).toString());
		} else {
			logger.debug("Creating subscription on " + situation.toString());
			subscriptions.put(situation, new Subscription(srsCommunicator, ownAddress, situation));
		}

	}

	/**
	 * Removes the subscription. Only deletes the subscription, if the last
	 * subscription required by the situation handler is deleted.
	 *
	 * @param situation
	 *            the situation for unsubscription
	 */
	void removeSubscription(Situation situation) {
		if (subscriptions.containsKey(situation)) {
			Subscription subscription = subscriptions.get(situation);
			subscription.removeSubsription(false);
			if (!subscription.subsriptionsAvailable()) {
				deleteSubscription(situation);
			} else {
				logger.trace("Removed one subscription on " + situation);
				logger.trace(subscriptions.get(situation).toString());
			}
		}
	}

	/**
	 * Deletes a subscription.
	 *
	 * @param situation
	 *            the situation to unsubscribe.
	 */
	private void deleteSubscription(Situation situation) {
		logger.debug("Deleting subscription on " + situation.toString());
		subscriptions.remove(situation);
	}

	/**
	 * Creates a subscription for each rule/handledSituation in the directory.
	 * Can be used to initially create all subscriptions.
	 */
	void reloadSubscriptions() {
		subscriptions.clear();
		// do subscriptions for all rules
		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();
		for (Rule rule : rsa.getAllRules()) {
			subscribe(rule.getSituation());
		}

		// do subscriptions for all situations handled by endpoints
		EndpointStorageAccess esa = StorageAccessFactory.getEndpointStorageAccess();
		for (Endpoint endpoint : esa.getAllEndpoints()) {
			for (HandledSituation handledSituation : endpoint.getSituations()) {
				subscribe(new Situation(handledSituation.getSituationName(), handledSituation.getObjectName()));
			}
		}

		logger.debug("Subscriptions reloaded:\n" + getSubscriptionsAsString());
	}

	/**
	 * Deletes all subscriptions.
	 */
	void deleteAllSubscriptions() {
		logger.debug("Deleting all subscriptions.");
		for (Situation situation : subscriptions.keySet()) {
			subscriptions.get(situation).removeSubsription(true);
			deleteSubscription(situation);
		}
	}

	/**
	 * Gets the subscriptions as string. Gives an overview of all current
	 * subscriptions as nicely (?) readable string.
	 *
	 * @return the subscriptions as string
	 */
	String getSubscriptionsAsString() {
		StringBuilder sb = new StringBuilder();
		sb.append("-----------Subscriptions:-----------");
		sb.append(System.getProperty("line.separator"));
		sb.append("Total Number of Subscriptions: " + subscriptions.size());
		sb.append(System.getProperty("line.separator"));
		for (Situation situation : subscriptions.keySet()) {
			sb.append(situation.toString() + " --> " + subscriptions.get(situation).toString());
			sb.append(System.getProperty("line.separator"));
		}
		sb.append("-----------End Subscriptions-----------");
		sb.append(System.getProperty("line.separator"));
		return sb.toString();
	}

}

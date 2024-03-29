package situationManagement;

import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import main.CamelUtil;
import situationHandling.storage.datatypes.Situation;

/**
 * The Class Subscription is a wrapper for a single subscription. A subscription
 * tracks the number of times a subscription was added removed and gives the
 * information when the subscription can be finally deleted. The subscription is
 * associated to a certain situation.
 * <p>
 * The Subscription also takes care of the communication with the srs to do the
 * subscriptions and unsubscriptions.
 * <p>
 * In case it is not possible to subscribe to the SRS, it is tried until the
 * subscription is removed.
 */
class Subscription {

    /**
     * The logger for this class.
     */
    private static final Logger logger = Logger.getLogger(Subscription.class);

    /** The subscription count. The number of times a subscription was made. */
    private int subscriptionCount = 0;

    /** The srs communicator to create/delete subscriptions. */
    private final SRSCommunicator srsCommunicator;

    /**
     * The address of the situation handler. The situation changes will be sent
     * to this URL.
     */
    private final URL ownAddress;

    /**
     * The situation of this subscription.
     */
    private final Situation subscribeSituation;

    /**
     * The Executor service to run the subscriber thread.
     */
    private ExecutorService subscriberPool = CamelUtil.getCamelExecutorService();

    /**
     * The result of the subscriber thread.
     */
    private Future<Boolean> subscriberResult;

    /**
     * Creates a new subscription. Creates a subscription at the srs.
     */
    Subscription(SRSCommunicator srsCommunicator, URL ownAddress, Situation subscribeSituation) {
	subscriptionCount = 1;
	this.srsCommunicator = srsCommunicator;
	this.ownAddress = ownAddress;
	this.subscribeSituation = subscribeSituation;
	Subscriber subscriber = new Subscriber(this);
	subscriberResult = subscriberPool.submit(subscriber);
    }

    /**
     * Gives the information if this subscription is still required.
     *
     * @return true, if it is required. When false, the subscription can be
     *         deleted.
     */
    boolean subsriptionsAvailable() {
	return subscriptionCount > 0;
    }

    /**
     * Adds a subscription.
     */
    void addSubscription() {
	subscriptionCount++;
    }

    /**
     * Removes a subsription. Removes the subscription from the SRS, if this was
     * the last subscription or permanentlyy is set to true.
     * 
     * @param permanently
     *            true, if the subscription is to be permanently removed, i.e.
     *            the subscription is also deleted from the srs.
     */
    void removeSubsription(boolean permanently) {
	subscriptionCount--;
	if (!subsriptionsAvailable() || permanently) {
	    try {
		// chancel subscriber if not done
		if (!subscriberResult.isDone()) {
		    subscriberResult.cancel(true);
		}

		// if subscriber done, get result and unsubscribe in case of
		// success
		if (!subscriberResult.isCancelled() && subscriberResult.get()) {
		    srsCommunicator.unsubscribe(subscribeSituation, ownAddress);
		}
		onSubscriptionCreated();
	    } catch (InterruptedException | ExecutionException e) {
		logger.error("Error when deleting subscription.", e);
	    }
	}
    }

    /**
     * Callback when the subscription was successfully created.
     */
    void onSubscriptionCreated() {
	//method seems obsolete
    }

    /**
     * @return the srsCommunicator used to contact the srs.
     */
    SRSCommunicator getSrsCommunicator() {
	return srsCommunicator;
    }

    /**
     * @return the address of the situation handler
     */
    URL getOwnAddress() {
	return ownAddress;
    }

    /**
     * @return the situation of this subscription
     */
    Situation getSubscribeSituation() {
	return subscribeSituation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Subscription [subscriptionCount=" + subscriptionCount + "]";
    }

}

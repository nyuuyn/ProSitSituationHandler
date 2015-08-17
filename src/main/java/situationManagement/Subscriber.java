package situationManagement;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

/**
 * This class is used to create subscriptions at the srs. Implements callable.
 * The subscribe is tried until successful or the thread is interrupted. Tightly
 * interacts with an instance of {@link Subscription}.
 * 
 * 
 * @author Stefan
 *
 */
class Subscriber implements Callable<Boolean> {
	/**
	 * The logger
	 */
	private static final Logger logger = Logger.getLogger(Subscriber.class);

	/**
	 * The subscription this subscriber tries to create at the srs. The
	 * subscription is created for the situation specified by the subscription.
	 */
	private Subscription toSubscribe;

	/**
	 * Creates a new instance of Subscriber.
	 * 
	 * @param toSubscribe
	 *            The subscription this subscriber tries to create at the srs.
	 *            The subscription is created for the situation specified by the
	 *            subscription.
	 */
	Subscriber(Subscription toSubscribe) {
		this.toSubscribe = toSubscribe;
	}

	/**
	 * Tries to subscribe at the srs until it was successful or it is
	 * interrupted.
	 * 
	 * @return true, when the subscription was successful.
	 */
	@Override
	public Boolean call() throws Exception {
		while (!toSubscribe.getSrsCommunicator().subscribe(toSubscribe.getSubscribeSituation(),
				toSubscribe.getOwnAddress())) {
			logger.warn("Subscription on " + toSubscribe.getSubscribeSituation() + " failed. Trying again later.");
			try {
				Thread.sleep(30_000);
			} catch (InterruptedException e) {
				logger.debug("No subscription was created for " + toSubscribe.getSubscribeSituation());
				return false;
			}
		}

		toSubscribe.onSubscriptionCreated();

		return true;
	}

}

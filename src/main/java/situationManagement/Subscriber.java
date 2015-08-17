package situationManagement;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

class Subscriber implements Callable<Boolean> {

	Subscription toSubscribe;

	private static final Logger logger = Logger.getLogger(Subscriber.class);

	/**
	 * @param toSubscribe
	 */
	Subscriber(Subscription toSubscribe) {
		this.toSubscribe = toSubscribe;
	}

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

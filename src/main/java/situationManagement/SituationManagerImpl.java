package situationManagement;

import org.apache.log4j.Logger;

import situationHandling.storage.datatypes.Situation;

class SituationManagerImpl implements SituationManager {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(SituationManagerImpl.class);

	private SubscriptionHandler subscriptionHandler;
	private SRSCommunicator srsCommunicator;

	SituationManagerImpl(SubscriptionHandler subscriptionHandler,
			SRSCommunicator srsCommunicator) {
		this.subscriptionHandler = subscriptionHandler;
		this.srsCommunicator = srsCommunicator;
	}

	@Override
	public boolean situationOccured(Situation situation) {
		// check cache
		// query SRS if situation not cached
		SituationResult situationResult = srsCommunicator
				.getSituation(situation);

		if (situationResult != null) {
			logger.debug("Got situation state from SRS. Result " + situation
					+ " State: " + situationResult.isOccured());
			return situationResult.isOccured();
		} else {
			logger.warn("Failed getting situation from SRS: " + situation);
			return false;
		}
	}

	@Override
	public void subscribeOnSituation(Situation situation) {
		subscriptionHandler.subscribe(situation);

	}

	@Override
	public void removeSubscription(Situation situation) {
		subscriptionHandler.removeSubscription(situation);
	}

	@Override
	public void init() {
		subscriptionHandler.reloadSubscriptions();
	}

}

package situationManagement;

import org.apache.log4j.Logger;

import situationHandling.storage.datatypes.Situation;

/**
 * The Class SituationManagerImpl is the standard implementation of the
 * {@code SituationManager} Interface. It does not use a cache for lookups (and
 * also it does not update the cache).
 */
class SituationManagerImpl implements SituationManager {

	/** The logger for this class. */
	private final static Logger logger = Logger.getLogger(SituationManagerImpl.class);

	/** The subscription handler. */
	private SubscriptionHandler subscriptionHandler;

	/** The srs communicator. */
	private SRSCommunicator srsCommunicator;

	/**
	 * Instantiates a new situation manager. Requires an instance of
	 * {@code SubscriptionHandler} to manage subscriptions and an instance of
	 * {@code SRSCommunicator} to query the SRS.
	 *
	 * @param subscriptionHandler
	 *            the subscription handler to use
	 * @param srsCommunicator
	 *            the srs communicator to use.
	 */
	SituationManagerImpl(SubscriptionHandler subscriptionHandler, SRSCommunicator srsCommunicator) {
		this.subscriptionHandler = subscriptionHandler;
		this.srsCommunicator = srsCommunicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationManagement.SituationManager#situationOccured(situationHandling.
	 * storage.datatypes.Situation)
	 */
	@Override
	public boolean situationOccured(Situation situation) {
		// check cache
		// query SRS if situation not cached
		SituationResult situationResult = srsCommunicator.getSituation(situation);

		if (situationResult != null) {
			logger.debug(
					"Got situation state from SRS. Result " + situation + " State: " + situationResult.isOccured());
			return situationResult.isOccured();
		} else {
			logger.warn("Failed getting situation from SRS: " + situation);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationManagement.SituationManager#subscribeOnSituation(
	 * situationHandling.storage.datatypes.Situation)
	 */
	@Override
	public void subscribeOnSituation(Situation situation) {
		subscriptionHandler.subscribe(situation);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationManagement.SituationManager#removeSubscription(situationHandling
	 * .storage.datatypes.Situation)
	 */
	@Override
	public void removeSubscription(Situation situation) {
		subscriptionHandler.removeSubscription(situation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationManagement.SituationManager#init()
	 */
	@Override
	public void init() {
		subscriptionHandler.reloadSubscriptions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationManagement.SituationManager#cleanup()
	 */
	@Override
	public void cleanup() {
		subscriptionHandler.deleteAllSubscriptions();
	}

}

package situationManagement;

import java.util.Map;

import org.apache.log4j.Logger;

import situationHandling.storage.datatypes.Situation;

/**
 * The Class SituationManagerWithCache is an implementation
 * {@code SituationManager} Interface. It uses the cache for lookups (and also
 * updates the cache).
 *
 *
 * @author Stefan
 */
public class SituationManagerWithCache extends SituationManagerImpl {

	/** The situation cache. */
	private Map<Situation, Boolean> situationCache;

	/** The logger for this class. */
	private final static Logger logger = Logger.getLogger(SituationManagerWithCache.class);

	/**
	 * Instantiates a new situation manager with cache. Requires an instance of
	 * {@code SubscriptionHandler} to manage subscriptions and an instance of
	 * {@code SRSCommunicator} to query the SRS. Furthermore the cache to use is
	 * required.
	 *
	 * @param subscriptionHandler
	 *            the subscription handler to use.
	 * @param srsCommunicator
	 *            the srs communicator to use.
	 * @param situationCache
	 *            the situation cache to use.
	 */
	public SituationManagerWithCache(SubscriptionHandler subscriptionHandler, SRSCommunicator srsCommunicator,
			Map<Situation, Boolean> situationCache) {
		super(subscriptionHandler, srsCommunicator);
		this.situationCache = situationCache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationManagement.SituationManagerImpl#situationOccured(
	 * situationHandling .storage.datatypes.Situation)
	 */
	@Override
	public boolean situationOccured(Situation situation) {
		// check cache
		if (situationCache.containsKey(situation)) {
			logger.debug("Getting entry from situation cache: " + situation.toString());
			return situationCache.get(situation);
		} else {
			// query SRS if situation not cached (and insert in cache)
			boolean situationState = super.situationOccured(situation);
			updateSituationCache(situation, situationState);
			return situationState;
		}
	}

	/**
	 * Updates the situation cache.
	 *
	 * @param situation
	 *            the situation that is updated
	 * @param situationState
	 *            the new situation state. Use true to express that the
	 *            situation appeared and false to express that the situation
	 *            disappeared.
	 */
	void updateSituationCache(Situation situation, Boolean situationState) {
		logger.debug("Updating Cache: " + situation.toString() + " State: " + situationState);
		situationCache.put(situation, situationState);
	}

}

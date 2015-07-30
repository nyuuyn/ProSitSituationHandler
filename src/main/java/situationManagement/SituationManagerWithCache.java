/**
 * 
 */
package situationManagement;

import java.util.Map;

import org.apache.log4j.Logger;

import situationHandling.storage.datatypes.Situation;

/**
 * @author Stefan
 *
 */
public class SituationManagerWithCache extends SituationManagerImpl {

	private Map<Situation, Boolean> situationCache;

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(SituationManagerWithCache.class);

	/**
	 * @param subscriptionHandler
	 * @param srsCommunicator
	 */
	public SituationManagerWithCache(SubscriptionHandler subscriptionHandler,
			SRSCommunicator srsCommunicator,
			Map<Situation, Boolean> situationCache) {
		super(subscriptionHandler, srsCommunicator);
		this.situationCache = situationCache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationManagement.SituationManagerImpl#situationOccured(situationHandling
	 * .storage.datatypes.Situation)
	 */
	@Override
	public boolean situationOccured(Situation situation) {
		// check cache
		if (situationCache.containsKey(situation)) {
			logger.debug("Getting entry from situation cache: "
					+ situation.toString());
			return situationCache.get(situation);
		} else {
			// query SRS if situation not cached
			boolean situationState = super.situationOccured(situation);
			updateSituationCache(situation, situationState);
			return situationState;
		}
	}

	void updateSituationCache(Situation situation, Boolean situationState) {
		logger.debug("Updating Cache: " + situation.toString() + " State: "
				+ situationState);
		situationCache.put(situation, situationState);
	}

}

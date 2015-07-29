package situationManagement;

import situationHandling.storage.datatypes.Situation;

class SituationManagerImpl implements SituationManager {

	private SubscriptionHandler subscriptionHandler;
	private SRSCommunicator srsCommunicator;

	SituationManagerImpl(SubscriptionHandler subscriptionHandler,
			SRSCommunicator srsCommunicator) {
		this.subscriptionHandler = subscriptionHandler;
		this.srsCommunicator = srsCommunicator;
	}

	@Override
	public boolean situationOccured(Situation situation) {
		SituationResult situationResult = srsCommunicator
				.getSituation(situation);
		if (situationResult != null) {
			return situationResult.isOccured();
		} else {
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

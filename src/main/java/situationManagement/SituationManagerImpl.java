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
		// TODO:Remove this
		// |
		// \ /
		subscriptionHandler.toString();
		// TODO Auto-generated method stub
		// subscription nachschauen und adden

	}

	@Override
	public void removeSubscription(Situation situation) {
		// TODO Auto-generated method stub
		// subscription nachschauen und entfernen

	}

}

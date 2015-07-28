package situationManagement;

import java.util.Map;

import situationHandling.storage.datatypes.Situation;

class SituationManagerImpl implements SituationManager {

	private Map<Situation, SubscriptionHandler> subscriptions;

	SituationManagerImpl(Map<Situation, SubscriptionHandler> subscriptions) {
		this.subscriptions = subscriptions;
	}

	@Override
	public boolean situationOccured(Situation situation) {
		if (situation.getSituationName().equals("test")){
			return true;
		}
		return Math.random() > 0.5;

	}

	@Override
	public void subscribeOnSituation(Situation situation) {
		// TODO Auto-generated method stub
		// subscription nachschauen und adden

	}

	@Override
	public void removeSubscription(Situation situation) {
		// TODO Auto-generated method stub
		// subscription nachschauen und entfernen

	}

}

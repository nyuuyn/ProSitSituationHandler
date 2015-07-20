package situationManagement;

import java.util.Map;

import situationHandling.storage.datatypes.Situation;

class SituationManagerImpl implements SituationManager {
	
	private Map <Situation, SubscriptionHandler> subscriptions;
	
	SituationManagerImpl (Map <Situation, SubscriptionHandler> subscriptions){
		this.subscriptions = subscriptions;
	}

	@Override
	public boolean situationOccured() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void subscribeOnSituation() {
		// TODO Auto-generated method stub
		//subscription nachschauen und adden
		

	}

	@Override
	public void removeSubscription() {
		// TODO Auto-generated method stub
		//subscription nachschauen und entfernen

	}

}

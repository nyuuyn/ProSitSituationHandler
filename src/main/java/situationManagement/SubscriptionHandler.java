package situationManagement;

import situationHandling.storage.datatypes.Situation;

class SubscriptionHandler {

	private int subscriptionCount = 0;
	private Situation situation;

	SubscriptionHandler(Situation situation) {
		this.situation = situation;
	}
	
	public void addSubscription(){
		if (subscriptionCount == 0){
			//subscribe
		}
		subscriptionCount++;
	}
	
	public void removeSubscription(){
		subscriptionCount--;
		if (subscriptionCount == 0){
			//TODO: Unsubscribe
		}
	}

//	@Override
//	public boolean equals(Object object) {
//		if (object instanceof SubscriptionHandler){
//			SubscriptionHandler subscriptionHandler = (SubscriptionHandler) object;
//			//TODO: Equals Methode von situation
//			if (subscriptionHandler.situation.equals(this.situation)){
//				return true;
//			}
//		}
//		return false;
//
//	}

	
	
	

}

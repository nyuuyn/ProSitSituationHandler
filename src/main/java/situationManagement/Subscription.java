package situationManagement;

class Subscription {

	private int subscriptionCount = 0;

	Subscription() {
		subscriptionCount++;
	}

	boolean subsriptionsAvailable() {
		return subscriptionCount > 0;
	}
	
	void addSubscription(){
		subscriptionCount++;
	}
	
	void removeSubsription(){
		subscriptionCount--;
	}
}

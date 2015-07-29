package situationManagement;

class Subscription {

	private int subscriptionCount = 0;

	Subscription() {
		subscriptionCount = 1;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Subscription [subscriptionCount=" + subscriptionCount + "]";
	}
	
	
}

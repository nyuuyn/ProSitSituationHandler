package situationManagement;

/**
 * The Class Subscription is a wrapper for a single subscription. A subscription
 * tracks the number of times a subscription was added removed and gives the
 * information when the subscription can be finally deleted. The subscription
 * itself is not associated to a situation.
 */
class Subscription {

	/** The subscription count. The number of times a subscription was made. */
	private int subscriptionCount = 0;

	/**
	 * Creates a new subscription.
	 */
	Subscription() {
		subscriptionCount = 1;
	}

	/**
	 * Gives the information if this subscription is still required.
	 *
	 * @return true, if it is required. When false, the subscription can be deleted.
	 */
	boolean subsriptionsAvailable() {
		return subscriptionCount > 0;
	}

	/**
	 * Adds a subscription.
	 */
	void addSubscription() {
		subscriptionCount++;
	}

	/**
	 * Removes a subsription.
	 */
	void removeSubsription() {
		subscriptionCount--;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Subscription [subscriptionCount=" + subscriptionCount + "]";
	}

}

package situationManagement;

import situationHandling.storage.datatypes.Situation;

/**
 * The Interface SituationManager gives access to the functionality of the
 * Situation Management Component. It allows to:
 * 
 * <ol>
 * <li>Query the state of a situation from the SRS</li>
 * <li>Subscribe on situation changes</li>
 * <li>Unsubscribe from situation changes</li>
 * </ol>
 * 
 * @see situationManagement
 */
public interface SituationManager {

	/**
	 * Queries the state of a situation, i.e. if the situation has occured or
	 * not. Checks the cache, if the cache is enabled.
	 *
	 * @param situation
	 *            the situation to check
	 * @return true, if the situation occurred, false if the situation did not
	 *         occur.
	 */
	public boolean situationOccured(Situation situation);

	/**
	 * Subscribe on the changes of a certain situation. After subscription, the
	 * Situation Handler receives notifications from the SRS when a situation
	 * changed. The Situation Manager will automatically deliver the
	 * notifications about changes to the situation handling component.
	 * <p>
	 * If the subscribe method is used several times on a situation, still only
	 * one subscription on the situation is made. However, if the subscription
	 * is deleted, the delete is only forwarded to the SRS, if the subscription
	 * is not needed by the situation handler anymore (i.e. when no further
	 * rules, handled situations exist).
	 * <p>
	 * The method is intended to use when a new rule or endpoint (Handled
	 * Situations) is created.
	 *
	 * @param situation
	 *            the situation to subscribe on
	 */
	public void subscribeOnSituation(Situation situation);

	/**
	 * Removes the subscription on a situation. Using this method does not
	 * guarantee the the subscription is also deleted at the SRS. It is only
	 * deleted when there are no rules and endpoints for this situation remain.
	 * <p>
	 * The method is intended to use when a rule or endpoint (Handled
	 * Situations) is deleted.
	 *
	 * @param situation
	 *            the situation to unsubscribe
	 */
	public void removeSubscription(Situation situation);

	/**
	 * Inits the situation management. Especially creates subscriptions on all
	 * situations required by rules and endpoints.
	 */
	public void init();

	/**
	 * Does the cleanup before shutting down the situation managemnt component.
	 * Especially deletes all subscriptions.
	 */
	public void cleanup();

}

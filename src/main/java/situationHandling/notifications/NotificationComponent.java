package situationHandling.notifications;

import situationHandling.storage.datatypes.Situation;

/**
 * The Interface NotificationComponent exposes the functionality of the
 * notification component to other components.
 * <p>
 * Main Task of the notification component is to executed situation dependent
 * actions.
 * 
 * @see situationHandling.notifications
 */
public interface NotificationComponent {

    /**
     * This method is used to inform the NotificationComponent about a situation
     * change. When a situation changes, it is checked whether there are any
     * actions to be executed for this new situation. The actions are then
     * executed automatically.
     *
     * @param situation
     *            the situation that changed.
     * @param state
     *            the new state of the situation, i.e. true when the situation
     *            appeared or false when the situation disappeared.
     */
    public void situationChanged(Situation situation, boolean state);

    /**
     * Does necessary cleanup for a graceful shutdown;
     * 
     */
    public void shutdown();

}


/**
 * 
 * This package provides the classes that implement the forwarding of
 * notifications or the execution of arbitrary actions when situations change.
 * <p>
 * Actions/Notficiations are executed using Plugins. Actions can be specified
 * using the RestApi or the GUI.
 * <p>
 * The execution of actions is triggered by situation changes. To inform the
 * notification component about a sitaution change, the Interface
 * {@link situationHandling.notifications.NotificationComponent} is used. An instance
 * of the interface can be obtained using the
 * {@link situationHandling.notifications.NotificationComponentFactory}.
 * <p>
 * When a situation changes, all actions associated with the situation and the
 * new state of the situation will be triggered.
 * 
 * @author Stefan
 *
 * @see situationHandling.storage.datatypes.Action
 * @see situationHandling.storage.datatypes.Situation
 * @see situationHandler.plugin.Plugin
 */
package situationHandling.notifications;
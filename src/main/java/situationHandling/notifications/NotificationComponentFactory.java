package situationHandling.notifications;

/**
 * A factory for creating NotificationComponent objects. Use this factory to get
 * access to the functionality of the notification component.
 */
public class NotificationComponentFactory {

    /**
     * Gets an instance of the notification component.
     *
     * @return the notification component.
     */
    public static NotificationComponent getOperationComponent() {
	return new NotificationComponentImpl();
    }

}

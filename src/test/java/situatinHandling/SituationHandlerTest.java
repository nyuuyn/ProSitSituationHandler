package situatinHandling;

import situationHandling.notifications.NotificationComponent;
import situationHandling.notifications.NotificationComponentFactory;
import situationHandling.storage.datatypes.Situation;

public class SituationHandlerTest {

	public static void main(String[] args) {
		NotificationComponent notificationComponent = NotificationComponentFactory.getNotificationComponent();
		
		notificationComponent.situationChanged(new Situation("situation1", "object1"), true);
		notificationComponent.situationChanged(new Situation("situation2", "object1"), true);

	}

}

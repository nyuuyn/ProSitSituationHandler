package situationHandling.notifications;

public class NotificationComponentFactory {
	
	public static NotificationComponent getSituationHandler(){
		return new NotificationComponentImpl();
	}

}

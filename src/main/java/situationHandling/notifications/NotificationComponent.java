package situationHandling.notifications;

import situationHandling.storage.datatypes.Situation;

public interface NotificationComponent {
	
	public void situationChanged (Situation situation, boolean state);

}

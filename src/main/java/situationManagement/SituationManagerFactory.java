package situationManagement;

import java.util.HashMap;
import java.util.Map;

import situationHandling.storage.datatypes.Situation;

public class SituationManagerFactory {
	
	private static Map <Situation, SubscriptionHandler> subscriptions = new HashMap<>();
	
	public static SituationManager getSituationManager(){
		return new SituationManagerImpl(subscriptions);
	}

}

package situatinHandling;

import situationHandling.notifications.SituationHandler;
import situationHandling.notifications.SituationHandlerFactory;
import situationHandling.storage.datatypes.Situation;

public class SituationHandlerTest {

	public static void main(String[] args) {
		SituationHandler situationHandler = SituationHandlerFactory.getSituationHandler();
		
		situationHandler.situationChanged(new Situation("situation1", "object1"), true);
		situationHandler.situationChanged(new Situation("situation2", "object1"), true);

	}

}

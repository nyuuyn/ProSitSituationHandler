package situatinHandling;

import situationHandling.SituationHandler;
import situationHandling.SituationHandlerFactory;
import situationHandling.storage.datatypes.Situation;

public class SituationHandlerTest {

	public static void main(String[] args) {
		SituationHandler situationHandler = SituationHandlerFactory.getSituationHandler();
		
		situationHandler.handleSituation(new Situation("situation1", "object1"));
		situationHandler.handleSituation(new Situation("situation2", "object1"));

	}

}

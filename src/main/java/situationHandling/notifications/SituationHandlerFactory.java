package situationHandling.notifications;

public class SituationHandlerFactory {
	
	public static SituationHandler getSituationHandler(){
		return new SituationHandlerImpl();
	}

}

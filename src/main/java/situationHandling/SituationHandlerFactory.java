package situationHandling;

public class SituationHandlerFactory {
	
	public static SituationHandler getSituationHandler(){
		return new SituationHandlerImpl();
	}

}

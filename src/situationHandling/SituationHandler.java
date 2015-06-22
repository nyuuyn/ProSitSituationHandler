package situationHandling;

import situationManagement.Situation;

public interface SituationHandler {
	
	public void operationCall(String payload);
	
	public void situationOccured (Situation situation);
	
	

}

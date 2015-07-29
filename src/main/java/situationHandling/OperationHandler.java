package situationHandling;

import situationHandling.storage.datatypes.Situation;


public interface OperationHandler {
	
	public OperationHandlingResult handleOperation(String payload, String qualifier);
	
	public void situationChanged (Situation situation, boolean state);
		
	

}

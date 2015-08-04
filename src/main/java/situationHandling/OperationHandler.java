package situationHandling;

import situationHandling.storage.datatypes.Situation;


public interface OperationHandler {
	
	public OperationHandlingResult handleOperation(SoapMessage soapMessage, String qualifier);
	
	public void situationChanged (Situation situation, boolean state);
		
	

}

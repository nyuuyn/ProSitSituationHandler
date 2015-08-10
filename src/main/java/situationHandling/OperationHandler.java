package situationHandling;

import situationHandling.storage.datatypes.Situation;


public interface OperationHandler {
	
	public OperationHandlingResult handleOperation(SoapMessage soapMessage);
	
	public void situationChanged (Situation situation, boolean state);
		
	

}

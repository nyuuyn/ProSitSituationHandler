package situationHandling;

import situationHandling.storage.datatypes.Situation;


public interface OperationHandler {
	
	public OperationHandlingResult handleOperation(SoapMessage soapMessage, RollbackHandler rollbackHandler);
	
	public void situationChanged (Situation situation, boolean state);
	
	public void onAnswerReceived (SoapMessage soapMessage);
		
	

}

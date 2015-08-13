package situationHandling.workflowOperations;

import situationHandling.storage.datatypes.Situation;
import utils.soap.WsaSoapMessage;


public interface OperationHandler {
	
	public void handleOperation(WsaSoapMessage wsaSoapMessage, RollbackHandler rollbackHandler);
	
	public void situationChanged (Situation situation, boolean state);
	
	public void onAnswerReceived (WsaSoapMessage wsaSoapMessage);
		
	

}

package situationHandling;

import org.apache.camel.Exchange;

import situationManagement.Situation;

public interface SituationHandler {
	
	public void receivedOperationCall(Exchange exchange);
	
	public void situationOccured (Situation situation);
	
	

}

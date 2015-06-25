package situationHandling;

import org.apache.camel.Exchange;

import situationHandling.storage.datatypes.Situation;

public interface SituationHandler {
	
	public void receivedOperationCall(Exchange exchange);
	
	public void situationOccured (Situation situation);
	
	

}

package situationHandling;

import org.apache.camel.Exchange;

public interface OperationHandler {
	
	public void receivedOperationCall(Exchange exchange);
		
	

}

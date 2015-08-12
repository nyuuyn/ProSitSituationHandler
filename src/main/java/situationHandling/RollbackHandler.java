package situationHandling;

import java.util.List;

import org.apache.log4j.Logger;

import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Situation;

public class RollbackHandler {

	private static final Logger logger = Logger
			.getLogger(RollbackHandler.class);

	private String surrogateId;
	private Endpoint endpoint;
	private int rollbackCount = 0;
	private final int maxRollbacks;
	private WsaSoapMessage orignalMessage;

	/**
	 * @param surrogateId
	 * @param endpoint
	 * @param rollbackCount
	 * @param maxRollbacks
	 * @param orignalMessage
	 */
	RollbackHandler(Endpoint endpoint, int maxRollbacks,
			WsaSoapMessage orignalMessage, String surrogateId) {
		this.surrogateId = surrogateId;
		this.endpoint = endpoint;
		this.maxRollbacks = maxRollbacks;
		this.orignalMessage = orignalMessage;
	}

	String initiateRollback() {

		rollbackCount++;
		logger.debug("Initiating Rollback number " + rollbackCount +": Message " + surrogateId + " "
				+ endpoint.toString());
		if (rollbackCount > maxRollbacks) {
			return null;
		}
		
		WsaSoapMessage rollbackRequest = SoapRequestFactory.createRollbackRequest(
				endpoint.getEndpointURL(), surrogateId);
		new MessageRouter(rollbackRequest).forwardRollbackRequest();

		return rollbackRequest.getWsaMessageID();
	}

	void onRollbackCompleted(WsaSoapMessage wsaSoapMessage) {
		// TODO: Rollback Fault handeln
		logger.debug("Rollback completed: Message " + surrogateId + " "
				+ endpoint.toString());
		// init handling
		OperationHandlerFactory.getOperationHandler().handleOperation(
				orignalMessage, this);
	}

	List<Situation> getSituations() {
		return endpoint.getRollbackSituations();
	}

	void setSurrogateId(String surrogateId) {
		this.surrogateId = surrogateId;
	}
	
	void setEndpoint (Endpoint endpoint){
		this.endpoint = endpoint;
	}
	
	String getSurrogateId (){
		return surrogateId;
	}

}

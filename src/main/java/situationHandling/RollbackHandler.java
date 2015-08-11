package situationHandling;

import java.util.List;
import java.util.UUID;

import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Situation;

public class RollbackHandler {

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
			WsaSoapMessage orignalMessage) {
		this.surrogateId = orignalMessage.getWsaMessageID();
		this.endpoint = endpoint;
		this.maxRollbacks = maxRollbacks;
		this.orignalMessage = orignalMessage;
	}

	String initiateRollback() {
		rollbackCount++;
		if (rollbackCount > maxRollbacks) {
			return null;
		}

		// MessageRouter.getRoutingTable().removeSurrogateId(surrogateId);
		new MessageRouter(null).forwardRollbackRequest();

		// TODO: define rollback message and forward it
		// TODO: Set new UUID

		UUID newSurrogateId = UUID.randomUUID();

		return newSurrogateId.toString();
	}

	void onRollbackCompleted(WsaSoapMessage wsaSoapMessage) {
		// MessageRouter.getRoutingTable().removeSurrogateId(soapMessage.getWsaMessageID());
		new MessageRouter(null).rollbackResponseReceived(wsaSoapMessage
				.getWsaMessageID());
		// init handling
		OperationHandlerFactory.getOperationHandler().handleOperation(
				orignalMessage, this);
	}

	List<Situation> getSituations() {
		return endpoint.getRollbackSituations();
	}

}

package situationHandling;

import java.util.List;

import javax.xml.soap.SOAPConstants;

import org.apache.log4j.Logger;

import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

public class RollbackHandler {

	private static final Logger logger = Logger
			.getLogger(RollbackHandler.class);

	private String surrogateId;
	private Endpoint endpoint;
	private int rollbackCount = 0;
	private final int maxRollbacks;
	private WsaSoapMessage originalMessage;

	/**
	 * @param surrogateId
	 * @param endpoint
	 * @param rollbackCount
	 * @param maxRollbacks
	 * @param originalMessage
	 */
	RollbackHandler(Endpoint endpoint, int maxRollbacks,
			WsaSoapMessage originalMessage, String surrogateId) {
		this.surrogateId = surrogateId;
		this.endpoint = endpoint;
		this.maxRollbacks = maxRollbacks;
		this.originalMessage = originalMessage;
	}

	String initiateRollback() {

		rollbackCount++;
		logger.debug("Initiating Rollback number " + rollbackCount
				+ ": Message " + surrogateId + " " + endpoint.toString());

		WsaSoapMessage rollbackRequest = SoapRequestFactory
				.createRollbackRequest(endpoint.getEndpointURL(), surrogateId);
		new MessageRouter(rollbackRequest).forwardRollbackRequest();
		// TODO: Den fall abdecken, wenn der Endpunkt plötzlich nicht mehr
		// erreicht wird? (Wichtig: hier müsste man noch alle möglichen Router einträge usw löschen!)
		return rollbackRequest.getWsaMessageID();
	}

	void onRollbackCompleted(WsaSoapMessage wsaSoapMessage) {
		if (wsaSoapMessage.getRollbackResult()) {// rollback success

			logger.debug("Rollback completed: Message " + surrogateId + " "
					+ endpoint.toString());
			if (rollbackCount > maxRollbacks) {
				logger.info("Maximum number of retries reached for: "
						+ endpoint);

				sendRollbackFailedMessage("Maximum number of rollbacks reached.");

			} else {
				// init handling
				OperationHandlerFactory.getOperationHandler().handleOperation(
						originalMessage, this);
			}
		} else {// rollback failed
			sendRollbackFailedMessage("Problems occured due to situation change. Tried rollback, but the endpoint failed in the process. "
					+ endpoint);
			logger.info("Problems occured due to situation change. Tried rollback, but the endpoint failed in the process. "
					+ endpoint);
		}
	}

	private void sendRollbackFailedMessage(String errorText) {
		WsaSoapMessage errorMessage = SoapRequestFactory.createFaultMessageWsa(
				originalMessage.getWsaReplyTo().toString(), originalMessage
						.getWsaMessageID(),
				new Operation(originalMessage.getOperationName(),
						originalMessage.getNamespace()), errorText,
				SOAPConstants.SOAP_SENDER_FAULT);

		new MessageRouter(errorMessage).forwardFaultMessage(surrogateId);
	}

	List<Situation> getSituations() {
		return endpoint.getRollbackSituations();
	}

	void setSurrogateId(String surrogateId) {
		this.surrogateId = surrogateId;
	}

	void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	WsaSoapMessage getOriginalMessage() {
		return this.originalMessage;
	}

	String getSurrogateId() {
		return surrogateId;
	}

}

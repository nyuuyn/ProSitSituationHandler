package situationHandling.workflowOperations;

import java.util.List;

import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;

import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Situation;
import utils.soap.RollbackResponse;
import utils.soap.SoapRequestFactory;
import utils.soap.WsaSoapMessage;

/**
 * The Class RollbackHandler does the handling for rollbacks of single
 * operations. Each rollback handler is associated to a single request.
 * <p>
 * It is responsible for handling the rollback, the answer of the rollback and
 * possible errors.
 */
class RollbackHandler {

    /** The logger. */
    private static final Logger logger = Logger.getLogger(RollbackHandler.class);

    /** The request this handler is responsible for. */
    private WsaSoapMessage originalMessage;

    /** The surrogate id that was last assigned to the request. */
    private String surrogateId;

    /** The last endpoint the request was sent to. */
    private Endpoint endpoint;

    /** The number of times a rollback was performed. */
    private int rollbackCount = 0;

    /** The maximum number of rollbacks allowed for this request. */
    private final int maxRollbacks;

    /**
     * Instantiates a new rollback handler.
     *
     * @param endpoint
     *            The last endpoint the request was sent to.
     * @param maxRollbacks
     *            The maximum number of rollbacks allowed for this request
     * @param originalMessage
     *            The request this handler is responsible for.
     * @param surrogateId
     *            The surrogate id that was last assigned to the request.
     */
    RollbackHandler(Endpoint endpoint, int maxRollbacks, WsaSoapMessage originalMessage,
	    String surrogateId) {
	this.surrogateId = surrogateId;
	this.endpoint = endpoint;
	this.maxRollbacks = maxRollbacks;
	this.originalMessage = originalMessage;
    }

    /**
     * Initiate rollback, i.e. send a rollback message to the endpoint that is
     * currently processing the request.
     *
     * @return the id of the rollback request
     */
    String initiateRollback() {

	rollbackCount++;
	logger.debug("Initiating Rollback number " + rollbackCount + ": Message " + surrogateId
		+ " " + endpoint.toString());

	WsaSoapMessage rollbackRequest = SoapRequestFactory
		.createRollbackRequest(endpoint.getEndpointURL(), surrogateId);
	new MessageRouter(rollbackRequest).forwardRollbackRequest();
	return rollbackRequest.getWsaMessageID();
    }

    /**
     * On rollback completed. This method must be called when the rollback was
     * completed. It initiates further steps. Either the request is handled
     * again or a fault is sent to the original requester.
     *
     * @param rollbackAnswer
     *            the answer
     */
    void onRollbackCompleted(WsaSoapMessage rollbackAnswer) {
	RollbackResponse responseMessage = null;
	try {
	    responseMessage = new RollbackResponse(rollbackAnswer);
	} catch (SOAPException e) {
	    rollbackFailed();
	}
	if (responseMessage.getRollbackResult()) {// rollback success
	    logger.debug("Rollback completed: Message " + surrogateId + " " + endpoint.toString());
	    if (rollbackCount > maxRollbacks) {
		String resultMessage = "Maximum number of retries reached for: "
			+ endpoint.toString();
		logger.info(resultMessage);
		sendRollbackFailedMessage(resultMessage);
		StorageAccessFactory.getHistoryAccess().appendWorkflowRollbackAnswer(endpoint,
			false, resultMessage);
	    } else {
		// init handling
		OperationHandlerFactory.getOperationHandlerWithRollback()
			.handleOperation(originalMessage, this);
		StorageAccessFactory.getHistoryAccess().appendWorkflowRollbackAnswer(endpoint,
			true, "");
	    }
	} else {// rollback failed
	    rollbackFailed();
	}
    }

    /**
     * Handles the case that a rollback failed (should usually not happen!)
     * 
     */
    private void rollbackFailed() {
	String resultMessage = "Problems occured due to situation change. Tried rollback, but the endpoint failed in the process. "
		+ endpoint.toString();
	sendRollbackFailedMessage(resultMessage);
	logger.info(resultMessage);
	StorageAccessFactory.getHistoryAccess().appendWorkflowRollbackAnswer(endpoint, false,
		resultMessage);
    }

    /**
     * Helper method to send a "rollback failed" message.
     *
     * @param errorText
     *            the error text. Should contain a message that describes why
     *            the rollback failed.
     */
    private void sendRollbackFailedMessage(String errorText) {
	// use the correlation id if specified or the message id else
	String correlationId = originalMessage.getFaultCorrelationId() == null
		? originalMessage.getWsaMessageID() : originalMessage.getFaultCorrelationId();
	WsaSoapMessage errorMessage = SoapRequestFactory.createFaultMessageWsa(
		originalMessage.getWsaFaultTo().toString(), correlationId, errorText,
		SOAPConstants.SOAP_SENDER_FAULT);

	new MessageRouter(errorMessage).forwardFaultMessage(surrogateId);
    }

    /**
     * Gets the situations in which a rollback is executed by this handler.
     *
     * @return the situations
     */
    List<Situation> getSituations() {
	return endpoint.getRollbackSituations();
    }

    /**
     * Sets the surrogate id that was last assigned to the request.
     *
     * @param surrogateId
     *            the new surrogate id
     */
    void setSurrogateId(String surrogateId) {
	this.surrogateId = surrogateId;
    }

    /**
     * Sets the last endpoint the request was sent to.
     *
     * @param endpoint
     *            the new endpoint
     */
    void setEndpoint(Endpoint endpoint) {
	this.endpoint = endpoint;
    }

    /**
     * Gets the original message.
     *
     * @return The request this handler is responsible for.
     */
    WsaSoapMessage getOriginalMessage() {
	return this.originalMessage;
    }

    /**
     * Gets the surrogate id.
     *
     * @return The surrogate id that was last assigned to the request.
     */
    String getSurrogateId() {
	return surrogateId;
    }

    /**
     * Gets the endpoint.
     *
     * @return The last endpoint the request was sent to.
     */
    Endpoint getEndpoint() {
	return this.endpoint;
    }

}

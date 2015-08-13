package situationHandling.workflowOperations;

import java.util.List;

import javax.xml.soap.SOAPConstants;

import org.apache.log4j.Logger;

import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;
import utils.soap.SoapRequestFactory;
import utils.soap.WsaSoapMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class RollbackHandler.
 */
class RollbackHandler {

    /** The Constant logger. */
    private static final Logger logger = Logger.getLogger(RollbackHandler.class);

    /** The surrogate id. */
    private String surrogateId;

    /** The endpoint. */
    private Endpoint endpoint;

    /** The rollback count. */
    private int rollbackCount = 0;

    /** The max rollbacks. */
    private final int maxRollbacks;

    /** The original message. */
    private WsaSoapMessage originalMessage;

    /**
     * Instantiates a new rollback handler.
     *
     * @param endpoint
     *            the endpoint
     * @param maxRollbacks
     *            the max rollbacks
     * @param originalMessage
     *            the original message
     * @param surrogateId
     *            the surrogate id
     */
    RollbackHandler(Endpoint endpoint, int maxRollbacks, WsaSoapMessage originalMessage,
	    String surrogateId) {
	this.surrogateId = surrogateId;
	this.endpoint = endpoint;
	this.maxRollbacks = maxRollbacks;
	this.originalMessage = originalMessage;
    }

    /**
     * Initiate rollback.
     *
     * @return the string
     */
    String initiateRollback() {

	rollbackCount++;
	logger.debug("Initiating Rollback number " + rollbackCount + ": Message " + surrogateId
		+ " " + endpoint.toString());

	WsaSoapMessage rollbackRequest = SoapRequestFactory
		.createRollbackRequest(endpoint.getEndpointURL(), surrogateId);
	new MessageRouter(rollbackRequest).forwardRollbackRequest();

	// TODO: Den fall abdecken, wenn der Endpunkt plötzlich nicht mehr
	// erreicht wird? (Wichtig: hier müsste man noch alle möglichen Router
	// einträge usw löschen!)
	return rollbackRequest.getWsaMessageID();
    }

    /**
     * On rollback completed.
     *
     * @param wsaSoapMessage
     *            the wsa soap message
     */
    void onRollbackCompleted(WsaSoapMessage wsaSoapMessage) {
	if (wsaSoapMessage.getRollbackResult()) {// rollback success

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
	    }
	} else {// rollback failed
	    String resultMessage = "Problems occured due to situation change. Tried rollback, but the endpoint failed in the process. "
		    + endpoint.toString();
	    sendRollbackFailedMessage(resultMessage);
	    logger.info(resultMessage);
	    StorageAccessFactory.getHistoryAccess().appendWorkflowRollbackAnswer(endpoint, false,
		    resultMessage);
	}
    }

    /**
     * Send rollback failed message.
     *
     * @param errorText
     *            the error text
     */
    private void sendRollbackFailedMessage(String errorText) {
	WsaSoapMessage errorMessage = SoapRequestFactory.createFaultMessageWsa(
		originalMessage.getWsaReplyTo().toString(), originalMessage.getWsaMessageID(),
		new Operation(originalMessage.getOperationName(), originalMessage.getNamespace()),
		errorText, SOAPConstants.SOAP_SENDER_FAULT);

	new MessageRouter(errorMessage).forwardFaultMessage(surrogateId);
    }

    /**
     * Gets the situations.
     *
     * @return the situations
     */
    List<Situation> getSituations() {
	return endpoint.getRollbackSituations();
    }

    /**
     * Sets the surrogate id.
     *
     * @param surrogateId
     *            the new surrogate id
     */
    void setSurrogateId(String surrogateId) {
	this.surrogateId = surrogateId;
    }

    /**
     * Sets the endpoint.
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
     * @return the original message
     */
    WsaSoapMessage getOriginalMessage() {
	return this.originalMessage;
    }

    /**
     * Gets the surrogate id.
     *
     * @return the surrogate id
     */
    String getSurrogateId() {
	return surrogateId;
    }

    /**
     * Gets the endpoint.
     *
     * @return the endpoint
     */
    Endpoint getEndpoint() {
	return this.endpoint;
    }

}

package utils.soap;

import java.util.Iterator;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.w3c.dom.NodeList;

public class RollbackResponse {

    private WsaSoapMessage wsaSoapMessage;

    /** States whether this message is a response to a rollback or not. */
    private boolean rollbackResponse = false;

    /**
     * 
     * The result of the rollback. True, if rollback successful
     */
    private boolean rollbackResult = false;

    /**
     * Contains the id of the message the rollback request/response is related
     * to.
     */
    private String relatedRollbackRequestId = null;

    public RollbackResponse(WsaSoapMessage wsaSoapMessage) throws SOAPException {
	this.wsaSoapMessage = wsaSoapMessage;
	setRollbackResponse();
	if (rollbackResponse) {
	    parseRollbackResult();
	}
    }

    /**
     * Sets the rollback response value to true, if the relationshipType of the
     * relatesTo header indicates that this is the response to a rollback.
     */
    private void setRollbackResponse() {
	String wsaRelationshipType = wsaSoapMessage.getWsaRelationshipType();
	if (wsaRelationshipType != null
		&& wsaRelationshipType.equals(SoapConstants.ROLLBACK_RESPONSE_RELATIONSHIP_TYPE)) {
	    rollbackResponse = true;
	}
    }

    /**
     * Parses the result of a rollback operation. Use only when the message is
     * the response message to a rollback message.
     * 
     * @throws SOAPException
     *             invalid soap message
     */
    private void parseRollbackResult() throws SOAPException {
	NodeList nl = wsaSoapMessage.getSoapMessage().getSOAPPart().getEnvelope().getBody()
		.getElementsByTagNameNS(SoapConstants.ROLLBACK_MESSAGE_NAMESPACE,
			SoapConstants.ROLLBACK_RESPONSE_ELEMENT);
	if (nl == null) {
	    throw new SOAPException();
	}

	SOAPElement testEl = (SOAPElement) nl.item(0);

	@SuppressWarnings("rawtypes")
	Iterator it = testEl.getChildElements();
	while (it.hasNext()) {
	    Object next = it.next();
	    if (next instanceof SOAPElement) {
		SOAPElement childEL = (SOAPElement) next;
		if (childEL.getNodeName()
			.equals(SoapConstants.ROLLBACK_MESSAGE_RELATED_ID_ELEMENT)) {
		    relatedRollbackRequestId = childEL.getValue();
		} else if (childEL.getNodeName()
			.equals(SoapConstants.ROLLBACK_MESSAGE_SUCCESS_ELEMENT)) {
		    rollbackResult = Boolean.parseBoolean(childEL.getValue());
		}
	    }
	}

    }

    /**
     * Checks if the message is a rollback response.
     *
     * @return true, if it is a rollback response
     */
    public boolean isRollbackResponse() {
	return rollbackResponse;
    }

    /**
     * Gets the rollback result.
     *
     * @return the rollbackResult. True if the rollback was successful
     */
    public boolean getRollbackResult() {
	return rollbackResult;
    }

    /**
     * 
     * @return the id of the message the rollback request/response is related
     *         to.
     */
    public String getRelatedRollbackRequestId() {
	return relatedRollbackRequestId;
    }

}

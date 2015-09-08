package utils.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//TODO: Diese Klasse ist eine Schande --> viel zu gross und universal...

/**
 * The Class WsaSoapMessage is a wrapper class for soap messages. It provides
 * means to directly access and set relevant headers, especially WSA headers and
 * headers handled by the situation handler.
 */
public class WsaSoapMessage {

    /** The logger. */
    private static final Logger logger = Logger.getLogger(WsaSoapMessage.class);

    /** The soap message. */
    private SOAPMessage soapMessage;

    /** The name of the operation used in this soap message. */
    private String operationName = null;

    /** The namespace (of the operation). */
    private String namespace = null;

    /** The value of the wsa:messageId header. */
    private String wsaMessageID = null;

    /** The value of the wsa:replyTo address. */
    private URL wsaReplyTo = null;

    /** The value of wsa relationship type attribute. */
    private String wsaRelationshipType = null;

    /** The value of the wsa:To header. */
    private URL wsaTo = null;

    /** The value of the wsa:action header. */
    private String wsaAction = null;

    /** The value of the wsa:relatesTo header. */
    private String wsaRelatesTo = null;

    /**
     * The value of the wsa:FaultTo header.
     */
    private URL wsaFaultTo = null;

    /**
     * The id to use when sending a fault message. Has to be specified by the
     * first request.
     */
    private String faultCorrelationId = null;

    /** States whether this message is a response to a rollback or not. */
    private boolean rollbackResponse = false;

    /** States whether this message is a rollback request or not */
    private boolean rollbackRequest = false;

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

    /**
     * If this message is a request for a workflow operation, {@code maxRetries}
     * states the maximum number of retries when a rollback is required due to
     * situation change.
     */
    private Integer maxRetries = null;

    /**
     * Instantiates a new wsa soap message. The soap message is parsed from a
     * String.
     *
     * @param soapString
     *            the string that contains the soap message
     * @throws SOAPException
     *             in case {@code soapString} does not conatin a valid soap
     *             message.
     */
    public WsaSoapMessage(String soapString) throws SOAPException {
	InputStream inputStream = null;
	try {
	    inputStream = new ByteArrayInputStream(soapString.getBytes());

	    this.soapMessage = MessageFactory.newInstance().createMessage(null, inputStream);
	    parseWsaHeaders();
	    parseActorSpecificHeaders();
	    setRollbackResponse();
	    setRollbackRequest();
	    parseOperationName();
	    if (rollbackResponse) {
		parseRollbackResult();
	    }

	    inputStream.close();
	} catch (SOAPException | IOException e) {
	    throw new SOAPException(e);
	} finally {
	    closeQuietly(inputStream);
	}

    }

    /**
     * Parses the operation name from the soap message. It is assumed that the
     * operation is the first element in the soap body (text nodes are ignored).
     *
     * @throws SOAPException
     *             in case the soap operation could not be parsed.
     */
    private void parseOperationName() throws SOAPException {
	String qualifiedOperation;

	SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();
	
	// look for the element that represents the operation (there might be
	// empty text elements)
	NodeList topLevelElements = body.getChildNodes();

	Node operationNode = null;
	for (int i = 0; i < topLevelElements.getLength(); i++) {
	    operationNode = topLevelElements.item(i);
	    if (operationNode.getNodeType() == Node.ELEMENT_NODE) {
		break; // operation element found
	    }
	}

	qualifiedOperation = operationNode.getNodeName();
	String[] temp = qualifiedOperation.split(":");
	if (temp.length == 1) {// no namespace specified
	    this.operationName = temp[0];
	} else if (temp.length == 2) {// namespace specified
	    this.operationName = temp[1];
	    //parse namespaceprefix and get uri
	    String namespacePrefix = temp[0];
	    this.namespace = body.getNamespaceURI(namespacePrefix);
	} else {// invalid operation
	    throw new SOAPException("Invalid operation.");
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
	NodeList nl = soapMessage.getSOAPPart().getEnvelope().getBody().getElementsByTagNameNS(
		SoapConstants.ROLLBACK_MESSAGE_NAMESPACE, SoapConstants.ROLLBACK_RESPONSE_ELEMENT);
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
     * Parses all wsa headers contained in the message.
     *
     * @throws SOAPException
     *             in case the message is invalid
     * @throws MalformedURLException
     *             when the message contains invalid urls
     */
    @SuppressWarnings("rawtypes")
    private void parseWsaHeaders() throws SOAPException, MalformedURLException {

	SOAPHeader sh = soapMessage.getSOAPHeader();
	if (sh == null) {
	    throw new SOAPException("Message does not contain required Headers.");
	}

	Iterator it = sh.examineAllHeaderElements();
	while (it.hasNext()) {
	    SOAPHeaderElement she = (SOAPHeaderElement) it.next();

	    // parse wsa headers
	    if (she.getElementQName().getNamespaceURI().equals(SoapConstants.WSA_URI)) {
		String headerName = she.getElementQName().getLocalPart();
		switch (headerName) {
		case "Action":
		    this.wsaAction = she.getValue();
		    break;
		case "MessageID":
		    this.wsaMessageID = she.getValue();
		    break;
		case "To":
		    this.wsaTo = new URL(she.getValue());
		    break;
		case "ReplyTo":
		    this.wsaReplyTo = parseWsaAddress(she.getChildNodes());
		    break;
		case "RelatesTo":
		    this.wsaRelatesTo = she.getValue();
		    this.wsaRelationshipType = she.getAttributeValue(new QName("RelationshipType"));
		    break;
		case "FaultTo":
		    this.wsaFaultTo = parseWsaAddress(she.getChildNodes());
		    break;
		default:
		    break;
		}
	    }
	}
    }

    /**
     * Parses the headers that are specific to the actor "Situation_Handler"
     * (The mustUnderstand Attribute is ignored, so all headers for this role
     * are processed). If headers are found, they are removed.
     *
     * @throws SOAPException
     *             in case the message is invalid
     */
    @SuppressWarnings({ "rawtypes" })
    private void parseActorSpecificHeaders() throws SOAPException {
	SOAPHeader sh = soapMessage.getSOAPHeader();
	Iterator it = sh.examineHeaderElements(SoapConstants.SITUATION_HANDLER_ROLE);
	LinkedList<SOAPHeaderElement> toRemove = new LinkedList<>();
	while (it.hasNext()) {
	    SOAPHeaderElement she = (SOAPHeaderElement) it.next();
	    String headerName = she.getLocalName();
	    // maxRetries header
	    if (headerName.equals(SoapConstants.HEADER_MAX_RETRIES)) {
		try {
		    maxRetries = Integer.parseInt(she.getValue());
		    if (maxRetries < 0) {
			throw new SOAPException("Invalid Number of retries");
		    }
		} catch (NumberFormatException e) {
		    throw new SOAPException("Invalid Number of retries", e);
		}
		toRemove.add(she);
	    } else if (headerName.equals(SoapConstants.FAULT_CORRELATION_HEADER_NAME)) {
		// correlation info header
		faultCorrelationId = she.getValue();
		toRemove.add(she);
	    }
	}
	// remove headers after processing.. (to avoid modification of list
	// while iterating)
	for (SOAPHeaderElement she : toRemove) {
	    sh.removeChild(she);
	}

    }

    /**
     * Parses a wsa address header and the contained address that is nested
     * within another element. (For example To parse the address in the replyTo
     * header)
     *
     * @param addressElements
     *            the child elements of the header node that contains the
     *            address element.
     * @return the value contained in the address element as URL. Null, if no
     *         address element found.
     * @throws MalformedURLException
     *             if the address is malformed
     */
    private URL parseWsaAddress(NodeList addressElements) throws MalformedURLException {
	for (int i = 0; i < addressElements.getLength(); i++) {

	    Node current = addressElements.item(i);
	    // only address is parsed. Prefix can be ignored
	    // in this case
	    if (current.getNodeName().endsWith("Address")) {
		String parsedAddress = current.getChildNodes().item(0).getNodeValue();
		if (!parsedAddress.equals(SoapConstants.NO_REPLY_URI)) {
		    return new URL(current.getChildNodes().item(0).getNodeValue());
		}
	    }
	}
	return null;
    }

    /**
     * Sets the rollback response value to true, if the relationshipType of the
     * relatesTo header indicates that this is the response to a rollback.
     */
    private void setRollbackResponse() {
	if (wsaRelationshipType != null
		&& wsaRelationshipType.equals(SoapConstants.ROLLBACK_RESPONSE_RELATIONSHIP_TYPE)) {
	    rollbackResponse = true;
	}
    }

    /**
     * Sets the rollback request value to true, if the relationshipType of the
     * relatesTo header indicates that this is a rollback request.
     */
    private void setRollbackRequest() {
	if (wsaRelationshipType != null
		&& wsaRelationshipType.equals(SoapConstants.RELATIONSHIP_TYPE_ROLLBACK)) {
	    rollbackRequest = true;
	}
    }

    /**
     * Sets the value of the address element of the wsa:ReplyTo header to the
     * specified value.
     *
     * @param replyAddress
     *            the new address
     */
    @SuppressWarnings("rawtypes")
    public void setWsaReplyTo(URL replyAddress) {
	try {
	    SOAPHeader sh = soapMessage.getSOAPHeader();
	    Iterator it = sh.examineAllHeaderElements();
	    boolean updated = false;
	    // look for the wsa:replyTo header
	    while (it.hasNext() && !updated) {
		SOAPHeaderElement she = (SOAPHeaderElement) it.next();

		if (she.getElementQName().getNamespaceURI().equals(SoapConstants.WSA_URI)
			&& she.getElementQName().getLocalPart().equals("ReplyTo")) {
		    NodeList childs = she.getChildNodes();
		    // look for the address element
		    for (int i = 0; i < childs.getLength(); i++) {
			Node current = childs.item(i);
			// other wsa:replyTo headers are ignored
			if (current.getNodeName().endsWith("Address")) {
			    // set value of only child
			    wsaReplyTo = replyAddress;
			    current.getChildNodes().item(0).setNodeValue(replyAddress.toString());
			    updated = true;
			    break;
			}
		    }
		}
	    }
	} catch (SOAPException e) {
	    logger.error("Error setting reply address", e);
	}

    }

    /**
     * Sets the value of the wsa:To header to the specified value.
     *
     * @param receiverAddress
     *            the new address
     */
    public void setWsaTo(URL receiverAddress) {
	if (setStandardWsaHeader("To", receiverAddress.toString())) {
	    this.wsaTo = receiverAddress;
	}
    }

    /**
     * Sets the value of the wsa:MessageID header to the specified value.
     *
     * @param messageId
     *            the new message id
     */
    public void setWsaMessageId(String messageId) {
	if (setStandardWsaHeader("MessageID", messageId)) {
	    this.wsaMessageID = messageId;
	}

    }

    /**
     * Sets the value of the wsa:RelatesTo header to the specified value.
     *
     * @param messageId
     *            the id of the related message.
     */
    public void setWsaRelatesTo(String messageId) {
	if (setStandardWsaHeader("RelatesTo", messageId)) {
	    this.wsaRelatesTo = messageId;
	}
    }

    /**
     * A helper method to set the value of wsa headers, that are not nested any
     * further.
     *
     * @param headerName
     *            the header to set (local name)
     * @param headerValue
     *            the header value
     * @return true, if successful, false if the header was not found
     */
    @SuppressWarnings("rawtypes")
    private boolean setStandardWsaHeader(String headerName, String headerValue) {
	try {
	    SOAPHeader sh = soapMessage.getSOAPHeader();

	    Iterator it = sh.examineAllHeaderElements();
	    while (it.hasNext()) {
		SOAPHeaderElement she = (SOAPHeaderElement) it.next();
		if (she.getElementQName().getNamespaceURI().equals(SoapConstants.WSA_URI)
			&& she.getElementQName().getLocalPart().equals(headerName)) {
		    she.setValue(headerValue);
		    return true;
		}
	    }
	} catch (SOAPException e) {
	    logger.error("Error setting header: " + headerName, e);
	}

	return false;
    }

    /**
     * Gets the soap message.
     *
     * @return the soap message
     */
    public String getSoapMessage() {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	try {
	    soapMessage.writeTo(out);
	} catch (SOAPException | IOException e) {
	    logger.error("Error converting soap message.", e);
	} finally {
	    closeQuietly(out);
	}
	return out.toString();
    }

    /**
     * Gets the name of the operation used in this soap message.
     *
     * @return the operationName, {@code Null} if the operation name could not
     *         be parsed
     */
    public String getOperationName() {
	return operationName;
    }

    /**
     * Gets the namespace (of the operation).
     *
     * @return the namespace{@code Null} if the namespace could not be parsed
     */
    public String getNamespace() {
	return namespace;
    }

    /**
     * Gets the value of the wsa:messageId header.
     *
     * @return the wsaMessageID, {@code null} if the header is not set
     */
    public String getWsaMessageID() {
	return wsaMessageID;
    }

    /**
     * Gets the value of the wsa:replyTo address.
     *
     * @return the wsaReplyTo, {@code null} if the header is not set
     */
    public URL getWsaReplyTo() {
	return wsaReplyTo;
    }

    /**
     * Gets the value of the wsa:action header.
     *
     * @return the wsaAction, {@code null} if the header is not set
     */
    public String getWsaAction() {
	return wsaAction;
    }

    /**
     * Gets the value of the wsa:To header
     *
     * @return the wsaTo, {@code null} if the header is not set
     */
    public URL getWsaTo() {
	return wsaTo;
    }

    /**
     * Gets the value of the wsa:relatesTo header.
     *
     * @return the wsaRelatesTo, {@code null} if the header is not set
     */
    public String getWsaRelatesTo() {
	return wsaRelatesTo;
    }

    /**
     * Gets the value of wsa relationship type attribute.
     *
     * @return the wsaRelationshipType, {@code null} if the header is not set
     */
    public String getWsaRelationshipType() {
	return wsaRelationshipType;
    }

    /**
     * Gets the address value of the wsa:FaultTo header contained in this
     * message.
     * 
     * @return the address contained in the header or null if this message does
     *         not contain that header.
     */
    public URL getWsaFaultTo() {
	return wsaFaultTo;
    }

    /**
     * 
     * Gets the correlation id that was specified by a request to use for fault
     * messages.
     * 
     * @return the correlation id or Null, if no id was specififed.
     */
    public String getFaultCorrelationId() {
	return faultCorrelationId;
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
     * Checks if the message is rollback request message.
     *
     * @return true, if it is a rollback request
     */
    public boolean isRollbackRequest() {
	return rollbackRequest;
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
     * Gets the max retries. If this message is a request for a workflow
     * operation, {@code maxRetries} states the maximum number of retries when a
     * rollback is required due to situation change.
     *
     * @return the maxRetries, {@code null} if the header is not set
     */
    public Integer getMaxRetries() {
	return maxRetries;
    }

    /**
     * 
     * @return the id of the message the rollback request/response is related
     *         to.
     */
    public String getRelatedRollbackRequestId() {
	return relatedRollbackRequestId;
    }

    @Override
    public String toString() {
	return "WsaSoapMessage [operationName=" + operationName + ", namespace=" + namespace
		+ ", wsaMessageID=" + wsaMessageID + ", wsaReplyTo=" + wsaReplyTo
		+ ", wsaRelationshipType=" + wsaRelationshipType + ", wsaTo=" + wsaTo
		+ ", wsaAction=" + wsaAction + ", wsaRelatesTo=" + wsaRelatesTo + ", wsaFaultTo="
		+ wsaFaultTo + ", rollbackResponse=" + rollbackResponse + ", rollbackRequest="
		+ rollbackRequest + ", rollbackResult=" + rollbackResult
		+ ", relatedRollbackRequestId=" + relatedRollbackRequestId + ", maxRetries="
		+ maxRetries + "]";
    }

    /**
     * A more compact implementation of toString(). Only gives the headers that
     * are set.
     *
     * @return the string
     */
    public String toStringCompact() {
	return "WsaSoapMessage ["
		+ (operationName != null ? "operationName=" + operationName + ", " : "")
		+ (namespace != null ? "namespace=" + namespace + ", " : "")
		+ (wsaMessageID != null ? "wsaMessageID=" + wsaMessageID + ", " : "")
		+ (wsaReplyTo != null ? "wsaReplyTo=" + wsaReplyTo + ", " : "")
		+ (wsaRelationshipType != null ? "wsaRelationshipType=" + wsaRelationshipType + ", "
			: "")
		+ (wsaTo != null ? "wsaTo=" + wsaTo + ", " : "")
		+ (wsaAction != null ? "wsaAction=" + wsaAction + ", " : "")
		+ (wsaRelatesTo != null ? "wsaRelatesTo=" + wsaRelatesTo + ", " : "")
		+ (wsaFaultTo != null ? "wsaFaultTo=" + wsaFaultTo + ", " : "")
		+ "rollbackResponse=" + rollbackResponse + ", rollbackRequest=" + rollbackRequest
		+ ", rollbackResult=" + rollbackResult + ", "
		+ (relatedRollbackRequestId != null
			? "relatedRollbackRequestId=" + relatedRollbackRequestId + ", " : "")
		+ (maxRetries != null ? "maxRetries=" + maxRetries : "") + "]";
    }

    /**
     * Helper method to quietly close streams etc. (without throwing
     * exceptions). Good for closing streams in finally blocks.
     * 
     * @param closableResource
     *            the stream etc to close
     */
    private void closeQuietly(Closeable closableResource) {
	try {
	    closableResource.close();
	} catch (IOException e) {
	    // if closing fails, the resource is probably closed anyway, so do
	    // nothing...
	}
    }
}

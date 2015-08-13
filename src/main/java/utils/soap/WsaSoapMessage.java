package utils.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    /** States whether this message is a response to a rollback or not. */
    private boolean rollbackResponse = false;

    /** States whether this message is a rollback request or not */
    private boolean rollbackRequest = false;

    /**
     * In case this message is the response to a rollback message,
     * {@code rollbackResult} states wheter the rollback was successful or not.
     */
    private boolean rollbackResult = false;

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

	// look for the element that represents the operation (there might be
	// empty text elements)
	NodeList operations = soapMessage.getSOAPPart().getEnvelope().getBody().getChildNodes();

	Node operationNode = null;
	for (int i = 0; i < operations.getLength(); i++) {
	    operationNode = operations.item(i);
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
	    this.namespace = temp[0];
	} else {// invalid operation
	    throw new SOAPException("Invalid operation.");
	}
	if (rollbackResponse) {// parse result of rollback
	    NodeList returnValues = operationNode.getChildNodes();
	    Node returnValue = null;
	    for (int i = 0; i < returnValues.getLength(); i++) {
		returnValue = returnValues.item(i);
		if (returnValue.getNodeType() == Node.ELEMENT_NODE) {
		    break; // return value found
		}
	    }
	    rollbackResult = Boolean.parseBoolean(returnValue.getFirstChild().getNodeValue());
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
	Iterator it = sh.examineAllHeaderElements();
	while (it.hasNext()) {// TODO: Hier noch den prefix von wsa parsen und
			      // nicht einfach annehmen, dass wsa verwendet
			      // wird
	    SOAPHeaderElement she = (SOAPHeaderElement) it.next();
	    String headerName = she.getTagName();
	    switch (headerName) {
	    case "wsa:Action":
		this.wsaAction = she.getValue();
		break;
	    case "wsa:MessageID":
		this.wsaMessageID = she.getValue();
		break;
	    case "wsa:To":
		this.wsaTo = new URL(she.getValue());
		break;
	    case "wsa:ReplyTo":
		parseReplyToHeader(she.getChildNodes());
		break;
	    case "wsa:RelatesTo":
		this.wsaRelatesTo = she.getValue();
		this.wsaRelationshipType = she.getAttributeValue(new QName("RelationshipType"));
		break;
	    case "MaxRetries":
		break;
	    default:
		break;
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
	while (it.hasNext()) {
	    SOAPHeaderElement she = (SOAPHeaderElement) it.next();
	    String headerName = she.getTagName();
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
		it.remove();
	    }

	}
    }

    /**
     * Parses the wsa:ReplyToHeader and sets the contained address.
     *
     * @param replyToElements
     *            the child elements of the wsa:replyTo header node.
     * @throws MalformedURLException
     *             if the address is malformed
     */
    private void parseReplyToHeader(NodeList replyToElements) throws MalformedURLException {
	for (int i = 0; i < replyToElements.getLength(); i++) {
	    Node current = replyToElements.item(i);
	    // other wsa:replyTo headers are not parsed
	    if (current.getNodeName().equalsIgnoreCase("wsa:Address")) {// TODO:
									// PRefix
		wsaReplyTo = new URL(current.getChildNodes().item(0).getNodeValue());
	    }
	}

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
		String headerName = she.getTagName();
		if (headerName.equals("wsa:ReplyTo")) {// TODO: PRefix
		    NodeList childs = she.getChildNodes();
		    // look for the address element
		    for (int i = 0; i < childs.getLength(); i++) {
			Node current = childs.item(i);
			// other wsa:replyTo headers are ignored
			if (current.getNodeName().equalsIgnoreCase("wsa:Address")) {// TODO:
										    // PRefix
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
	if (setStandardWsaHeader("wsa:To", receiverAddress.toString())) {// TODO:
									 // PRefix
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
	if (setStandardWsaHeader("wsa:MessageID", messageId)) {// TODO: PRefix
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
	if (setStandardWsaHeader("wsa:RelatesTo", messageId)) {// TODO: PRefix
	    this.wsaRelatesTo = messageId;
	}
    }

    /**
     * A helper method to set the value of wsa headers, that are not nested any
     * further.
     *
     * @param headerName
     *            the header to set
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
		String currentHeaderName = she.getTagName();
		if (currentHeaderName.equals(headerName)) {
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "WsaSoapMessage [operationName=" + operationName + ", namespace=" + namespace
		+ ", wsaMessageID=" + wsaMessageID + ", wsaTo=" + wsaTo + ", wsaAction=" + wsaAction
		+ ", wsaRelatesTo=" + wsaRelatesTo + ", wsaRelationshipType=" + wsaRelationshipType
		+ ", wsaReplyTo=" + wsaReplyTo + ", rollbackResponse=" + rollbackResponse
		+ ", rollbackRequest=" + rollbackRequest + ", rollbackResult=" + rollbackResult
		+ ", maxRetries=" + maxRetries + "]";
    }

    /**
     * A more compact implementation of toString(). Only gives the headers that
     * are set.
     *
     * @return the string
     */
    public String toStringCompact() {
	return "WsaSoapMessage [" + (namespace != null ? "namespace=" + namespace + ", " : "")
		+ (operationName != null ? "operationName=" + operationName + ", " : "")
		+ (wsaMessageID != null ? "wsaMessageID=" + wsaMessageID + ", " : "")
		+ (wsaTo != null ? "wsaTo=" + wsaTo + ", " : "")
		+ (wsaAction != null ? "wsaAction=" + wsaAction + ", " : "")
		+ (wsaReplyTo != null ? "wsaReplyTo=" + wsaReplyTo + ", " : "")
		+ (wsaRelatesTo != null ? "wsaRelatesTo=" + wsaRelatesTo + ", " : "")
		+ (wsaRelationshipType != null ? "wsaRelationshipType=" + wsaRelationshipType + ", "
			: "")
		+ "rollbackResponse=" + rollbackResponse + ", rollbackRequest=" + rollbackRequest
		+ ", " + "rollbackResult=" + rollbackResult + ", "
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

package utils.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import main.GlobalProperties;

import org.apache.log4j.Logger;

import situationHandling.storage.datatypes.Operation;

/**
 * A factory for creating soap requests. This is a utility class to create new
 * SOAP Requests that are sent by the situation handler.
 */
public class SoapRequestFactory {

    /** The logger. */
    private static final Logger logger = Logger.getLogger(SoapRequestFactory.class);

    /**
     * The name of the operation to start a rollback of a workflow operation.
     * The operation does not have any parameters.
     */
    private static final String ROLLBACK_START_OPERATION = "startRollback";

    /**
     * The name of the "main-element" in a rollback request.
     */
    private static final String ROLLBACK_START_OPERATION_ELEMENT = "RollbackRequestElement";

    /**
     * The prefix used for {@code ROLLBACK_MESSAGE_NAMESPACE}.
     */
    private static final String ROLLBACK_MESSAGE_NAMESPACE_PREFIX = "rol";

    /**
     * Creates a new rollback request that relates to a certain message. Sets
     * all required WSA headers.
     *
     * @param receiver
     *            the receiver of the message
     * @param relatedMessageId
     *            the id of the related message. The rollback request will
     *            relate to this id.
     * @param recipentNS
     *            the namespace of the recipent in a form that can be used for
     *            the soap action, i.e. the prefix for the
     *            soap action.
     * @return the rollback request
     */
    public static WsaSoapMessage createRollbackRequest(String receiver, String relatedMessageId,
	    String recipentNS) {
	try {
	    SOAPMessage msg = MessageFactory.newInstance().createMessage();
	    SOAPPart part = msg.getSOAPPart();
	    SOAPEnvelope envelope = part.getEnvelope();

	    envelope.addNamespaceDeclaration(ROLLBACK_MESSAGE_NAMESPACE_PREFIX,
		    SoapConstants.ROLLBACK_MESSAGE_NAMESPACE);

	    // create reply address
	    String ownIPAdress = InetAddress.getLocalHost().getHostAddress();
	    String replyToAddress = "http://" + ownIPAdress + ":" + GlobalProperties.NETWORK_PORT
		    + "/" + GlobalProperties.ANSWER_ENDPOINT_PATH;

	    addWsaHeaders(envelope, receiver, true, relatedMessageId,
		    SoapConstants.RELATIONSHIP_TYPE_ROLLBACK, replyToAddress,
		    recipentNS + "/" + ROLLBACK_START_OPERATION);

	    // TODO: Bei der Action muss der komplette Namespace stehen!
	    // (genauso wie vermutlich noch in der SOAP Nachricht selbst eine
	    // Deklaration des NS nötig ist. Den NS kann man vermutlich aus der
	    // originalen Request Rausparsen!)

	    // body
	    SOAPBody body = envelope.getBody();
	    SOAPBodyElement startRollbackElement = body.addBodyElement(envelope.createQName(
		    ROLLBACK_START_OPERATION_ELEMENT, ROLLBACK_MESSAGE_NAMESPACE_PREFIX));

	    SOAPElement releatedRequestIdElement = startRollbackElement.addChildElement(
		    envelope.createQName(SoapConstants.ROLLBACK_MESSAGE_RELATED_ID_ELEMENT,
			    ROLLBACK_MESSAGE_NAMESPACE_PREFIX));

	    releatedRequestIdElement.addTextNode(relatedMessageId);

	    return createTheMessage(msg);
	} catch (SOAPException | UnknownHostException e) {
	    logger.error("Error creating rollback request", e);
	    return null;
	} catch (IOException e) {
	    logger.error("Error converting soap message.", e);
	    return null;
	}

    }

    /**
     * Creates a new fault message. This fault message will relate to a certain
     * request. Uses Wsa.
     *
     * @param receiver
     *            the receiver of the fault message
     * @param relatedMessageId
     *            the id of the message this fault relates to
     * @param operation
     *            the operation of the request message that failed for some
     *            reason
     * @param errorMessage
     *            the error message
     * @param faultCode
     *            the fault code. A fault code according to the <a href =
     *            "http://www.w3.org/TR/2000/NOTE-SOAP-20000508/#_Toc478383510">
     *            soap specification<a>. Use a fault code of
     *            {@link SOAPConstants}.
     * @return the soap fault message
     * @see SOAPConstants
     */
    public static WsaSoapMessage createFaultMessageWsa(String receiver, String relatedMessageId,
	    Operation operation, String errorMessage, QName faultCode) {
	SOAPMessage msg;
	try {
	    msg = MessageFactory.newInstance().createMessage();
	    SOAPPart part = msg.getSOAPPart();
	    SOAPEnvelope envelope = part.getEnvelope();

	    addWsaHeaders(envelope, receiver, false, relatedMessageId,
		    SoapConstants.RELATIONSHIP_TYPE_RESPONSE, null,
		    "http://www.w3.org/2005/08/addressing/fault");

	    // add body
	    SOAPBody body = envelope.getBody();
	    body.addFault(faultCode, errorMessage);

	    return createTheMessage(msg);
	} catch (SOAPException e) {
	    logger.error("Error creating fault request", e);
	    return null;
	} catch (IOException e) {
	    logger.error("Error converting soap message.", e);
	    return null;
	}

    }

    /**
     * Creates a new fault message. This message does not directly relate to a
     * asynchronous request. Does not use wsa. Can be directly returned in
     * synchronous communication.
     * 
     * @param errorMessage
     *            the error message
     * @param faultCode
     *            the fault code. A fault code according to the <a href =
     *            "http://www.w3.org/TR/2000/NOTE-SOAP-20000508/#_Toc478383510">
     *            soap specification<a>. Use a fault code of
     *            {@link SOAPConstants}.
     * @return the fault message
     */
    public static WsaSoapMessage createFaultMessage(String errorMessage, QName faultCode) {
	SOAPMessage msg;
	try {
	    msg = MessageFactory.newInstance().createMessage();
	    SOAPPart part = msg.getSOAPPart();
	    SOAPEnvelope envelope = part.getEnvelope();

	    // add body
	    SOAPBody body = envelope.getBody();
	    body.addFault(faultCode, errorMessage);

	    return createTheMessage(msg);
	} catch (SOAPException e) {
	    logger.error("Error creating fault request", e);
	    return null;
	} catch (IOException e) {
	    logger.error("Error converting soap message.", e);
	    return null;
	}
    }

    /**
     * Adds the wsa headers.
     *
     * @param env
     *            the soap envelope element
     * @param receiver
     *            the receiver (URL) of the message
     * @param idRequired
     *            true, if a new id for the message is required
     * @param releatesToId
     *            the relatesTo id
     * @param relatesToType
     *            the relatesTo type
     * @param replyToAddress
     *            the reply to address
     * @param actionHeader
     *            the action header (wsa:Action)
     * @throws SOAPException
     * 
     */
    private static void addWsaHeaders(SOAPEnvelope env, String receiver, boolean idRequired,
	    String releatesToId, String relatesToType, String replyToAddress, String actionHeader)
		    throws SOAPException {

	SOAPHeader header = env.getHeader();
	header = (SOAPHeader) header.addNamespaceDeclaration(SoapConstants.DEFAULT_WSA_PREFIX,
		SoapConstants.WSA_URI);

	// to
	SOAPHeaderElement to = header
		.addHeaderElement(header.createQName("To", SoapConstants.DEFAULT_WSA_PREFIX));
	to.setValue(receiver);

	// action
	SOAPHeaderElement action = header
		.addHeaderElement(header.createQName("Action", SoapConstants.DEFAULT_WSA_PREFIX));
	action.setValue(actionHeader);

	// id
	if (idRequired) {
	    SOAPHeaderElement messageID = header.addHeaderElement(
		    header.createQName("MessageID", SoapConstants.DEFAULT_WSA_PREFIX));
	    messageID.setValue(UUID.randomUUID().toString());
	}

	// reply to
	if (replyToAddress != null) {
	    SOAPHeaderElement replyTo = header.addHeaderElement(
		    header.createQName("ReplyTo", SoapConstants.DEFAULT_WSA_PREFIX));
	    replyTo.addChildElement("Address", SoapConstants.DEFAULT_WSA_PREFIX)
		    .setValue(replyToAddress);
	}

	if (relatesToType != null && releatesToId != null) {
	    // relates to
	    SOAPHeaderElement relates = header.addHeaderElement(
		    header.createQName("RelatesTo", SoapConstants.DEFAULT_WSA_PREFIX));

	    relates.setValue(releatesToId);
	    relates.addAttribute(env.createName("RelationshipType"), relatesToType);
	}

    }

    /**
     * Creates a new SoapRequest object.
     *
     * @param msg
     *            the msg
     * @return the wsa soap message
     * @throws SOAPException
     *             the SOAP exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static WsaSoapMessage createTheMessage(SOAPMessage msg)
	    throws SOAPException, IOException {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	msg.writeTo(out);
	return new WsaSoapMessage(out.toString());
    }

}

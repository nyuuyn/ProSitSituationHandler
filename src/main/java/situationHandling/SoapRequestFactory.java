package situationHandling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;

import routes.GlobalProperties;
import situationHandling.storage.datatypes.Operation;

class SoapRequestFactory {

	private static Logger logger = Logger.getLogger(SoapRequestFactory.class);

	static WsaSoapMessage createRollbackRequest(String receiver,
			String relatedMessageId) {
		try {
			SOAPMessage msg = MessageFactory.newInstance().createMessage();

			SOAPPart part = msg.getSOAPPart();

			SOAPEnvelope envelope = part.getEnvelope();

			SOAPBody body = envelope.getBody();

			String ownIPAdress = InetAddress.getLocalHost().getHostAddress();
			String replyToAddress = "http://" + ownIPAdress + ":"
					+ GlobalProperties.NETWORK_PORT + "/"
					+ GlobalProperties.ANSWER_ENDPOINT_PATH;

			addWsaHeaders(envelope, receiver, true, relatedMessageId,
					SoapConstants.RELATIONSHIP_TYPE_ROLLBACK, replyToAddress,
					SoapConstants.ROLLBACK_START_OPERATION);

			// body
			body.addBodyElement(envelope
					.createName(SoapConstants.ROLLBACK_START_OPERATION));

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				msg.writeTo(out);
			} catch (SOAPException | IOException e) {
				logger.error("Error converting soap message.", e);
			}
			return new WsaSoapMessage(out.toString());
		} catch (SOAPException | UnknownHostException e) {
			logger.error("Error creating rollback request", e);
			return null;
		}

	}

	/**
	 * 
	 * @param receiver
	 * @param relatedMessageId
	 * @param operation
	 * @param errorMessage
	 * @param faultCode
	 * @return
	 * 
	 * @see SOAPConstants
	 */
	static WsaSoapMessage createFaultMessage(String receiver,
			String relatedMessageId, Operation operation, String errorMessage,
			QName faultCode) {
		SOAPMessage msg;
		try {
			msg = MessageFactory.newInstance().createMessage();

			SOAPPart part = msg.getSOAPPart();

			SOAPEnvelope envelope = part.getEnvelope();
			SOAPBody body = envelope.getBody();

			addWsaHeaders(
					envelope,
					receiver,
					false,
					relatedMessageId,
					SoapConstants.RELATIONSHIP_TYPE_RESPONSE,
					null,
					operation.getQualifier() + ":"
							+ operation.getOperationName() + "Response");

			// add body
			body.addFault(faultCode, errorMessage);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				msg.writeTo(out);
			} catch (SOAPException | IOException e) {
				logger.error("Error converting soap message.", e);
			}
			return new WsaSoapMessage(out.toString());

		} catch (SOAPException e) {
			logger.error("Error creating fault request", e);
			return null;
		}

	}

	private static void addWsaHeaders(SOAPEnvelope env, String receiver,
			boolean idRequired, String releatesToId, String relatesToType,
			String replyToAddress, String actionHeader) throws SOAPException {

		SOAPHeader header = env.getHeader();
		header = (SOAPHeader) header.addNamespaceDeclaration(
				SoapConstants.WSA_PREFIX,
				"http://www.w3.org/2005/08/addressing");

		// to
		SOAPHeaderElement to = header.addHeaderElement(header.createQName("To",
				SoapConstants.WSA_PREFIX));
		to.setValue(receiver);

		// action
		SOAPHeaderElement action = header.addHeaderElement(header.createQName(
				"Action", SoapConstants.WSA_PREFIX));
		action.setValue(actionHeader);

		// id
		if (idRequired) {
			SOAPHeaderElement messageID = header.addHeaderElement(header
					.createQName("MessageID", SoapConstants.WSA_PREFIX));
			messageID.setValue(UUID.randomUUID().toString());
		}

		// reply to
		if (replyToAddress != null) {
			SOAPHeaderElement replyTo = header.addHeaderElement(header
					.createQName("ReplyTo", SoapConstants.WSA_PREFIX));
			replyTo.addChildElement("Address", SoapConstants.WSA_PREFIX)
					.setValue(replyToAddress);
		}

		if (relatesToType != null && releatesToId != null) {
			// relates to
			SOAPHeaderElement relates = header.addHeaderElement(header
					.createQName("RelatesTo", SoapConstants.WSA_PREFIX));

			relates.setValue(releatesToId);
			relates.addAttribute(env.createName("RelationshipType"),
					relatesToType);
		}

	}

}

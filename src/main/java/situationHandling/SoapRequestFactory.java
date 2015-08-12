package situationHandling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;

import routes.GlobalProperties;

class SoapRequestFactory {
	
	private static Logger logger = Logger.getLogger(SoapRequestFactory.class);

	static WsaSoapMessage createRollbackRequest(String receiver,
			String relatedMessageId) {
		try {
			SOAPMessage msg = MessageFactory.newInstance().createMessage();

			SOAPPart part = msg.getSOAPPart();

			SOAPEnvelope envelope = part.getEnvelope();
			SOAPHeader header = envelope.getHeader();
			SOAPBody body = envelope.getBody();

			String wsaPrefix = "wsa";
			header = (SOAPHeader) header.addNamespaceDeclaration(wsaPrefix,
					"http://www.w3.org/2005/08/addressing");

			// headers
			// to
			SOAPHeaderElement to = header.addHeaderElement(header.createQName(
					"To", wsaPrefix));
			to.setValue(receiver);

			// reply to
			SOAPHeaderElement replyTo = header.addHeaderElement(header
					.createQName("ReplyTo", wsaPrefix));
			String ownIPAdress = InetAddress.getLocalHost().getHostAddress();
			replyTo.addChildElement("Address", wsaPrefix).setValue(
					"http://" + ownIPAdress + ":"
							+ GlobalProperties.NETWORK_PORT + "/"
							+ GlobalProperties.ANSWER_ENDPOINT_PATH);

			// id
			SOAPHeaderElement messageID = header.addHeaderElement(header
					.createQName("MessageID", wsaPrefix));

			messageID.setValue(UUID.randomUUID().toString());

			// relates to
			SOAPHeaderElement relates = header.addHeaderElement(header
					.createQName("RelatesTo", wsaPrefix));

			relates.setValue(relatedMessageId);
			relates.addAttribute(envelope.createName("RelationshipType"),
					SoapConstants.ROLLBACK_RELATIONSHIP_TYPE);

			// action

			SOAPHeaderElement action = header.addHeaderElement(header
					.createQName("Action", wsaPrefix));
			action.setValue(SoapConstants.ROLLBACK_START_OPERATION);

			// body
			body.addBodyElement(envelope.createName(SoapConstants.ROLLBACK_START_OPERATION));

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

}

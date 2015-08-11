package situationHandling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import routes.GlobalProperties;

public class WsaSoapMessage {

	private static final Logger logger = Logger.getLogger(WsaSoapMessage.class);

	private static final String ROLLBACK_RELATIONSHIP_TYPE = "Rollback";
	private static final String ROLLBACK_RESPONSE_RELATIONSHIP_TYPE = "RollbackResponse";
	private static final String ROLLBACK_START_OPERATION = "StartRollback";

	// TODO: Die ganzen WSA Header könnte man auch in einer Map oder so
	// zurückgeben, falls es noch mehr werden
	private SOAPMessage soapMessage;

	private String operationName = null;
	private String namespace = null;

	private String wsaMessageID = null;
	private URL wsaReplyTo = null;
	private String wsaRelationshipType = null;
	private URL wsaTo = null;
	private String wsaAction = null;
	private String wsaRelatesTo = null;

	private boolean rollbackResponse = false;
	private boolean rollbackRequest = false;
	private String rollbackResult = null;

	WsaSoapMessage(String soapString) throws SOAPException {

		try {
			InputStream inputStream = new ByteArrayInputStream(
					soapString.getBytes());

			this.soapMessage = MessageFactory.newInstance().createMessage(null,
					inputStream);
			parseWsaHeaders();
			setRollbackResponse();
			setRollbackRequest();
			parseOperationName();
			inputStream.close();
		} catch (SOAPException | IOException e) {
			throw new SOAPException(e);
		}

	}

	private void parseOperationName() throws SOAPException {
		String qualifiedOperation;

		// look for the element that represents the operation (there might be
		// empty text elements)
		NodeList operations = soapMessage.getSOAPPart().getEnvelope().getBody()
				.getChildNodes();
		Node operationNode = null;
		for (int i = 0; i < operations.getLength(); i++) {
			operationNode = operations.item(i);
			if (operationNode.getNodeType() == Node.ELEMENT_NODE) {
				break; // operation element found
			}

		}

		qualifiedOperation = operationNode.getNodeName();
		String[] temp = qualifiedOperation.split(":");
		if (temp.length == 1) {
			this.operationName = temp[0];
		} else if (temp.length == 2) {
			this.operationName = temp[1];
			this.namespace = temp[0];
		} else {
			throw new SOAPException("Invalid operation.");
		}
		if (rollbackResponse) {// parse result of rollback
			rollbackResult = operationNode.getFirstChild().getNodeValue();
		}
	}

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
				this.wsaRelationshipType = she.getAttributeValue(new QName(
						"RelationshipType"));
				break;
			default:
				break;
			}
		}
	}

	private void parseReplyToHeader(NodeList replyToElements)
			throws MalformedURLException {
		for (int i = 0; i < replyToElements.getLength(); i++) {
			Node current = replyToElements.item(i);
			// other wsa:replyTo headers are not parsed
			if (current.getNodeName().equalsIgnoreCase("wsa:Address")) {// TODO:
																		// PRefix
				wsaReplyTo = new URL(current.getChildNodes().item(0)
						.getNodeValue());
			}
		}

	}

	private void setRollbackResponse() {
		if (wsaRelationshipType != null
				&& wsaRelationshipType
						.equals(ROLLBACK_RESPONSE_RELATIONSHIP_TYPE)) {
			rollbackResponse = true;
		}
	}

	private void setRollbackRequest() {
		if (wsaRelationshipType != null
				&& wsaRelationshipType.equals(ROLLBACK_RELATIONSHIP_TYPE)) {
			rollbackRequest = true;
		}
	}

	@SuppressWarnings("rawtypes")
	void setWsaReplyTo(URL replyAddress) {
		try {
			SOAPHeader sh = soapMessage.getSOAPHeader();

			Iterator it = sh.examineAllHeaderElements();
			boolean updated = false;
			while (it.hasNext() && !updated) {
				SOAPHeaderElement she = (SOAPHeaderElement) it.next();
				String headerName = she.getTagName();
				if (headerName.equals("wsa:ReplyTo")) {// TODO: PRefix
					NodeList childs = she.getChildNodes();
					for (int i = 0; i < childs.getLength(); i++) {
						Node current = childs.item(i);
						// other wsa:replyTo headers are ignored
						if (current.getNodeName().equalsIgnoreCase(
								"wsa:Address")) {// TODO: PRefix
							// set value of only child
							wsaReplyTo = replyAddress;
							current.getChildNodes().item(0)
									.setNodeValue(replyAddress.toString());
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

	void setWsaTo(URL receiverAddress) {
		if (setStandardWsaHeader("wsa:To", receiverAddress.toString())) {// TODO:
																			// PRefix
			this.wsaTo = receiverAddress;
		}
	}

	void setWsaMessageId(String messageId) {
		if (setStandardWsaHeader("wsa:MessageID", messageId)) {// TODO: PRefix
			this.wsaMessageID = messageId;
		}

	}

	void setWsaRelatesTo(String messageId) {
		if (setStandardWsaHeader("wsa:RelatesTo", messageId)) {// TODO: PRefix
			this.wsaRelatesTo = messageId;
		}
	}

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

	String getSoapMessage() {
		// TODO: Streams schließen?
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			soapMessage.writeTo(out);
		} catch (SOAPException | IOException e) {
			logger.error("Error converting soap message.", e);
		}
		return out.toString();
	}

	/**
	 * @return the operationName
	 */
	String getOperationName() {
		return operationName;
	}

	/**
	 * @return the namespace
	 */
	String getNamespace() {
		return namespace;
	}

	/**
	 * @return the wsaMessageID
	 */
	String getWsaMessageID() {
		return wsaMessageID;
	}

	/**
	 * @return the wsaReplyTo
	 */
	URL getWsaReplyTo() {
		return wsaReplyTo;
	}

	/**
	 * @return the wsaAction
	 */
	String getWsaAction() {
		return wsaAction;
	}

	/**
	 * @return the wsaTo
	 */
	URL getWsaTo() {
		return wsaTo;
	}

	/**
	 * @return the wsaRelatesTo
	 */
	String getWsaRelatesTo() {
		return wsaRelatesTo;
	}

	/**
	 * @return the wsaRelationshipType
	 */
	String getWsaRelationshipType() {
		return wsaRelationshipType;
	}

	/**
	 * @return the rollbackResponse
	 */
	boolean isRollbackResponse() {
		return rollbackResponse;
	}

	/**
	 * @return the rollbackRequest
	 */
	boolean isRollbackRequest() {
		return rollbackRequest;
	}

	/**
	 * @return the rollbackResult
	 */
	String getRollbackResult() {
		return rollbackResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WsaSoapMessage [operationName=" + operationName
				+ ", namespace=" + namespace + ", wsaMessageID=" + wsaMessageID
				+ ", wsaReplyTo=" + wsaReplyTo + ", wsaTo=" + wsaTo
				+ ", wsaAction=" + wsaAction + ", wsaRelatesTo=" + wsaRelatesTo
				+ ", wsaRelationshipType=" + wsaRelationshipType + "]";
	}

	public static WsaSoapMessage createRollbackRequest(String receiver,
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
					"http://" + ownIPAdress + ":" + GlobalProperties.NETWORK_PORT + "/"
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
					ROLLBACK_RELATIONSHIP_TYPE);

			// action

			SOAPHeaderElement action = header.addHeaderElement(header
					.createQName("Action", wsaPrefix));
			action.setValue(ROLLBACK_START_OPERATION);

			// body
			body.addBodyElement(envelope.createName(ROLLBACK_START_OPERATION));

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

package situationHandling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

public class WsaSoapMessage {

	private static final Logger logger = Logger.getLogger(WsaSoapMessage.class);

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
	private boolean rollbackResult = false;

	private Integer maxRetries = null;

	WsaSoapMessage(String soapString) throws SOAPException {

		try {
			InputStream inputStream = new ByteArrayInputStream(
					soapString.getBytes());

			this.soapMessage = MessageFactory.newInstance().createMessage(null,
					inputStream);
			parseWsaHeaders();
			parseActorSpecificHeaders();
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
			case "MaxRetries":
				break;
			default:
				break;
			}
		}
	}

	@SuppressWarnings({ "rawtypes" })
	private void parseActorSpecificHeaders() throws SOAPException {
		SOAPHeader sh = soapMessage.getSOAPHeader();
		Iterator it = sh
				.examineHeaderElements(SoapConstants.SITUATION_HANDLER_ROLE);
		while (it.hasNext()) {
			SOAPHeaderElement she = (SOAPHeaderElement) it.next();
			String headerName = she.getTagName();
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
						.equals(SoapConstants.ROLLBACK_RESPONSE_RELATIONSHIP_TYPE)) {
			rollbackResponse = true;
		}
	}

	private void setRollbackRequest() {
		if (wsaRelationshipType != null
				&& wsaRelationshipType
						.equals(SoapConstants.RELATIONSHIP_TYPE_ROLLBACK)) {
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
	boolean getRollbackResult() {
		return rollbackResult;
	}

	/**
	 * @return the maxRetries
	 */
	Integer getMaxRetries() {
		return maxRetries;
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
				+ ", wsaTo=" + wsaTo + ", wsaAction=" + wsaAction
				+ ", wsaRelatesTo=" + wsaRelatesTo + ", wsaRelationshipType="
				+ wsaRelationshipType + ", wsaReplyTo=" + wsaReplyTo
				+ ", rollbackResponse=" + rollbackResponse
				+ ", rollbackRequest=" + rollbackRequest + ", rollbackResult="
				+ rollbackResult + ", maxRetries=" + maxRetries + "]";
	}

	public String toStringCompact() {
		return "WsaSoapMessage ["
				+ (namespace != null ? "namespace=" + namespace + ", " : "")
				+ (operationName != null ? "operationName=" + operationName
						+ ", " : "")
				+ (wsaMessageID != null ? "wsaMessageID=" + wsaMessageID + ", "
						: "")
				+ (wsaTo != null ? "wsaTo=" + wsaTo + ", " : "")
				+ (wsaAction != null ? "wsaAction=" + wsaAction + ", " : "")
				+ (wsaReplyTo != null ? "wsaReplyTo=" + wsaReplyTo + ", " : "")
				+ (wsaRelatesTo != null ? "wsaRelatesTo=" + wsaRelatesTo + ", "
						: "")
				+ (wsaRelationshipType != null ? "wsaRelationshipType="
						+ wsaRelationshipType + ", " : "")
				+ "rollbackResponse=" + rollbackResponse + ", rollbackRequest="
				+ rollbackRequest + ", " + "rollbackResult=" + rollbackResult
				+ ", " + (maxRetries != null ? "maxRetries=" + maxRetries : "")
				+ "]";
	}
}

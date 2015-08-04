package situationHandling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SoapMessage {

	private static final Logger logger = Logger.getLogger(SoapMessage.class);

	private SOAPMessage soapMessage;
	private String operationName = null;
	private String wsaMessageID = null;
	private String wsaReplyTo = null;
	private String wsaAction = null;

	SoapMessage(String soapString) throws SOAPException {

		try {
			InputStream inputStream = new ByteArrayInputStream(
					soapString.getBytes());

			this.soapMessage = MessageFactory.newInstance().createMessage(null,
					inputStream);
			parseOperationName();
			parseWsaHeaders();
			inputStream.close();
		} catch (SOAPException | IOException e) {
			throw new SOAPException(e);
		}

	}

	private void parseOperationName() throws SOAPException {

		// TODO: Hier muss man mal noch final klaren, was genau jetzt
		// eigentlich als qualifier benutzt wird: namespace oder porttype
		// (bei namespace kann man den ersten teil vom split nehmen)
		String qualifiedOperation;
		qualifiedOperation = soapMessage.getSOAPPart().getEnvelope().getBody()
				.getChildNodes().item(1).getNodeName();
		String[] temp = qualifiedOperation.split(":");
		this.operationName = temp[1];

		// logger.error("Error parsing operation name", e);

	}

	@SuppressWarnings("rawtypes")
	private void parseWsaHeaders() throws SOAPException {
		// try {
		SOAPHeader sh = soapMessage.getSOAPHeader();
		Iterator it = sh.examineAllHeaderElements();
		while (it.hasNext()) {
			SOAPHeaderElement she = (SOAPHeaderElement) it.next();
			String headerName = she.getTagName();
			switch (headerName) {
			case "wsa:Action":
				this.wsaAction = she.getValue();
				break;
			case "wsa:MessageID":
				this.wsaMessageID = she.getValue();
				break;
			case "wsa:to":
				this.wsaReplyTo = she.getValue();
			case "wsa:ReplyTo":
				parseReplyToHeader(she.getChildNodes());
				break;
			default:
				break;
			}
		}
		// } catch (SOAPException e) {
		// logger.error("Error parsing WSA headers", e);
		// }
	}

	private void parseReplyToHeader(NodeList replyToElements) {
		for (int i = 0; i < replyToElements.getLength(); i++) {
			Node current = replyToElements.item(i);
			// other wsa:replyTo headers are not parsed
			if (current.getNodeName().equalsIgnoreCase("wsa:Address")) {
				wsaReplyTo = current.getChildNodes().item(0).getNodeValue();
			}
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
				if (headerName.equals("wsa:ReplyTo")) {
					NodeList childs = she.getChildNodes();
					for (int i = 0; i < childs.getLength(); i++) {
						Node current = childs.item(i);
						// other wsa:replyTo headers are ignored
						if (current.getNodeName().equalsIgnoreCase(
								"wsa:Address")) {
							// set value of only child
							wsaReplyTo = replyAddress.toString();
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
	 * @return the wsaMessageID
	 */
	String getWsaMessageID() {
		return wsaMessageID;
	}

	/**
	 * @return the wsaReplyTo
	 */
	String getWsaReplyTo() {
		return wsaReplyTo;
	}

	/**
	 * @return the wsaAction
	 */
	String getWsaAction() {
		return wsaAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SoapProcessor [operationName=" + operationName
				+ ", wsaMessageID=" + wsaMessageID + ", wsaReplyTo="
				+ wsaReplyTo + ", wsaAction=" + wsaAction + "]";
	}

}

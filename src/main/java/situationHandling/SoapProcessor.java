package situationHandling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SoapProcessor {

	private static final Logger logger = Logger.getLogger(SoapProcessor.class);

	private SOAPMessage soapMessage;
	private String operationName = null;
	private String wsaMessageID = null;
	private String wsaReplyTo = null;
	private String wsaAction = null;

	SoapProcessor(String soapString) {
		InputStream inputStream = new ByteArrayInputStream(
				soapString.getBytes());

		try {
			this.soapMessage = MessageFactory.newInstance().createMessage(null,
					inputStream);
			parseOperationName();
			parseWsaHeaders();
		} catch (IOException | SOAPException e) {
			logger.error("Error parsing soap message", e);
		}

	}

	private void parseOperationName() {

		// TODO: Hier muss man mal noch final klaren, was genau jetzt
		// eigentlich als qualifier benutzt wird: namespace oder porttype
		// (bei namespace kann man den ersten teil vom split nehmen)
		String qualifiedOperation;
		try {
			qualifiedOperation = soapMessage.getSOAPPart().getEnvelope()
					.getBody().getChildNodes().item(1).getNodeName();
			String[] temp = qualifiedOperation.split(":");
			this.operationName = temp[1];
		} catch (SOAPException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	private void parseWsaHeaders() {
		try {
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
		} catch (SOAPException e) {
			e.printStackTrace();
		}
	}

	private void parseReplyToHeader(NodeList replyToElements) {
		for (int i = 0; i < replyToElements.getLength(); i++) {
			Node current = replyToElements.item(i);
			//other wsa:replyTo headers are not parsed
			if (current.getNodeName().equalsIgnoreCase("wsa:Address")) {
				wsaReplyTo = current.getChildNodes().item(0).getNodeValue();
			}
		}

	}

	String getSoapMessage() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			soapMessage.writeTo(out);
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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

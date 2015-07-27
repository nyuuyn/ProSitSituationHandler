package situationHandling;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

class SoapParser {

	static String getOperationName(String soapBody) {
		InputStream inputStream = new ByteArrayInputStream(soapBody.getBytes());
		String operation = "";
		try {
			SOAPMessage soapReq = MessageFactory.newInstance().createMessage(
					null, inputStream);
			// TODO: Hier muss man mal noch final klaren, was genau jetzt
			// eigentlich als qualifier benutzt wird: namespace oder porttype
			// (bei namespace kann man den ersten teil vom split nehmen)
			String qualifiedOperation = soapReq.getSOAPPart().getEnvelope()
					.getBody().getChildNodes().item(1).getNodeName();
			String[] temp = qualifiedOperation.split(":");
			operation = temp[1];

		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return operation;

	}

	static String getQualifier() {
		return null;
	}

}

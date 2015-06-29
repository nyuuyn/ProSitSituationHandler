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

			operation = soapReq.getSOAPPart().getEnvelope().getBody()
					.getChildNodes().item(1).getNodeName();

		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return operation;

	}
	
	static String getQualifier(){
		return null;
	}

}

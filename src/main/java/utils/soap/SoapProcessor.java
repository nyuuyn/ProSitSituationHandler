/**
 * 
 */
package utils.soap;

import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * @author Stefan
 *
 */
public class SoapProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		String body = exchange.getIn().getBody(String.class);

		WsaSoapMessage wsaSoapMessage;

		try {
			wsaSoapMessage = new WsaSoapMessage(body);
			exchange.getIn().setBody(wsaSoapMessage, WsaSoapMessage.class);
		} catch (SOAPException e) {
			WsaSoapMessage soapMessage = SoapRequestFactory.createFaultMessage(
					"Invalid SOAP Message. " + e.getMessage(),
					SOAPConstants.SOAP_SENDER_FAULT);
			exchange.getIn().setBody(soapMessage.getSoapMessage());
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			// stop route
			exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
		}

	}
}

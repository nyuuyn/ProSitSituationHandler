/**
 * 
 */
package situationHandling;

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

		SoapMessage soapProcessor;

		try {
			soapProcessor = new SoapMessage(body);
			exchange.getIn().setBody(soapProcessor, SoapProcessor.class);
		} catch (SOAPException e) {
			exchange.getIn().setBody("Invalid Soap Message");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			//stop route
			exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
		}

	}
}
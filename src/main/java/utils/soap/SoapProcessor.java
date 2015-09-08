package utils.soap;

import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/**
 * The Class SoapProcessor can be used as processor on a camel route and checks
 * the validity of a soap request, especially of requests that use wsa
 * addressing.
 * <p>
 * When the soap message is invalid, this processor creates a fault message with
 * an appropriate error message. When the soap message is valid, it converts the
 * body to an instance of {@link WsaSoapMessage}.
 *
 * @author Stefan
 */
public class SoapProcessor implements Processor {

    private static Logger logger = Logger.getLogger(SoapProcessor.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    @Override
    public void process(Exchange exchange) throws Exception {

	String body = exchange.getIn().getBody(String.class);
	logger.trace("Received Message:\n" + XMLPrinter.getPrettyXMLString(body, 2));
	WsaSoapMessage wsaSoapMessage;

	try {
	    wsaSoapMessage = new WsaSoapMessage(body);
	    exchange.getIn().setBody(wsaSoapMessage, WsaSoapMessage.class);
	} catch (SOAPException e) {
	    WsaSoapMessage soapMessage = SoapRequestFactory.createFaultMessage(
		    "Invalid SOAP Message. " + e.getMessage(), SOAPConstants.SOAP_SENDER_FAULT);
	    exchange.getIn().setBody(soapMessage.getSoapMessage());
	    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
	    // stop route
	    exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
	}

    }

}

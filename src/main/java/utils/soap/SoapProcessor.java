package utils.soap;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {

		String body = exchange.getIn().getBody(String.class);
		prettyPrintMessage(body, 2);
		WsaSoapMessage wsaSoapMessage;

		try {
			wsaSoapMessage = new WsaSoapMessage(body);
			exchange.getIn().setBody(wsaSoapMessage, WsaSoapMessage.class);
		} catch (SOAPException e) {
			WsaSoapMessage soapMessage = SoapRequestFactory
					.createFaultMessage("Invalid SOAP Message. " + e.getMessage(), SOAPConstants.SOAP_SENDER_FAULT);
			exchange.getIn().setBody(soapMessage.getSoapMessage());
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			// stop route
			exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
		}

	}

	/**
	 * Helper mehtod to pretty print xml.
	 * 
	 * @param message
	 *            the xml message as string
	 * @param indent
	 *            the number of indent spaces
	 */
	private void prettyPrintMessage(String message, int indent) {
		try {
			Source xmlInput = new StreamSource(new StringReader(message));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);
			System.out.println("Received message:\n" + xmlOutput.getWriter().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

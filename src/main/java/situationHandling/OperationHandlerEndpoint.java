/**
 * 
 */
package situationHandling;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.soap.SOAPException;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

/**
 * @author Stefan
 *
 */
public class OperationHandlerEndpoint {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(OperationHandlerEndpoint.class);

	public void receiveRequest(Exchange exchange) {
		SoapMessage soapMessage = exchange.getIn().getBody(SoapMessage.class);

		logger.debug("Received request:\n" + soapMessage.toString());

		String qualifier = exchange.getIn()
				.getHeader("CamelHttpPath", String.class).replace("/", "")
				.trim();

		OperationHandlerFactory.getOperationHandler().handleOperation(
				soapMessage, qualifier);

		// // TODO:useless
		// OperationHandlingResult result = OperationHandlingResult.success;
		//
		// // TODO: Hier muss eine Request an einen anderen Endpunkt gesendet
		// // werden! (Das Return hier ist momentan fürn Arsch! (bzw. eigentlich
		// // muss das Unten beim Receive Anser gemacht werden! Hier wäre dann
		// vllt
		// // vorgeschaltet noch ein Validity check schön!
		// if (result == OperationHandlingResult.success) {
		// exchange.getOut().setBody("");
		// } else if (result == OperationHandlingResult.noMatchFound) {
		// exchange.getOut().setBody("No matching endpoint found.");
		// exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		// } else if (result == OperationHandlingResult.error) {
		// exchange.getOut().setBody("Arbitrary error.");
		// exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
		// }

	}

	public void receiveAnswer(Exchange exchange) {
		SoapMessage sp = exchange.getIn().getBody(SoapMessage.class);

		System.out.println("\n" + sp.toString());
		try {
			sp.setWsaReplyTo(new URL("http://aenderung"));
			sp.setWsaTo(new URL("http://aenderung"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		System.out.println("\n" + sp.toString());

	}

}

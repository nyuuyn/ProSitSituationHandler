/**
 * 
 */
package situationHandling;

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

		OperationHandlerFactory.getOperationHandler().handleOperation(
				soapMessage, null);

		// // TODO:useless
		// OperationHandlingResult result = OperationHandlingResult.success;
		//
		// // TODO: Hier muss eine Request an einen anderen Endpunkt gesendet
		// // werden! (Das Return hier ist momentan f�rn Arsch! (bzw. eigentlich
		// // muss das Unten beim Receive Anser gemacht werden! Hier w�re dann
		// vllt
		// // vorgeschaltet noch ein Validity check sch�n!
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
		logger.debug("Received Answer Message: " + sp.toString());
		OperationHandlerFactory.getOperationHandler().onAnswerReceived(sp);
	}

}

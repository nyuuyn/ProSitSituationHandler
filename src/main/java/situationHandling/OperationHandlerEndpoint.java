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
		String body = exchange.getIn().getBody(String.class);
		
		logger.debug("Received request:/n"
				+ body);
		
		String qualifier = exchange.getIn()
				.getHeader("CamelHttpPath", String.class).replace("/", "")
				.trim();
		OperationHandlingResult result = OperationHandlerFactory
				.getOperationHandler().handleOperation(
						body, qualifier);

		if (result == OperationHandlingResult.success) {
			exchange.getOut().setBody("");
		} else if (result == OperationHandlingResult.noMatchFound) {
			exchange.getOut().setBody("No matching endpoint found.");
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		} else if (result == OperationHandlingResult.error) {
			exchange.getOut().setBody("Arbitrary error.");
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
		}
	}

	public void receiveAnswer(Exchange exchange) {

	}

}

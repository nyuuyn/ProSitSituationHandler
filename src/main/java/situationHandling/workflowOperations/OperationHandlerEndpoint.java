/**
 * 
 */
package situationHandling.workflowOperations;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import utils.soap.WsaSoapMessage;

/**
 * @author Stefan
 *
 */
public class OperationHandlerEndpoint {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(OperationHandlerEndpoint.class);

	public void receiveRequest(Exchange exchange) {
		WsaSoapMessage wsaSoapMessage = exchange.getIn().getBody(
				WsaSoapMessage.class);

		logger.debug("Received request:\n" + wsaSoapMessage.toString());

		OperationHandlerFactory.getOperationHandler().handleOperation(
				wsaSoapMessage, null);
	}

	public void receiveAnswer(Exchange exchange) {
		WsaSoapMessage sp = exchange.getIn().getBody(WsaSoapMessage.class);
		logger.debug("Received Answer Message: " + sp.toString());
		OperationHandlerFactory.getOperationHandler().onAnswerReceived(sp);
	}

}

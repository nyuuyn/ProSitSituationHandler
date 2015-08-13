/**
 * 
 */
package situationHandling.workflowOperations;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import utils.soap.WsaSoapMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class OperationHandlerEndpoint.
 *
 * @author Stefan
 */
public class OperationHandlerEndpoint {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(OperationHandlerEndpoint.class);

	/**
	 * Receive request.
	 *
	 * @param exchange the exchange
	 */
	public void receiveRequest(Exchange exchange) {
		WsaSoapMessage wsaSoapMessage = exchange.getIn().getBody(
				WsaSoapMessage.class);

		logger.debug("Received request:\n" + wsaSoapMessage.toString());

		OperationHandlerFactory.getOperationHandler().handleOperation(
				wsaSoapMessage);
	}

	/**
	 * Receive answer.
	 *
	 * @param exchange the exchange
	 */
	public void receiveAnswer(Exchange exchange) {
		WsaSoapMessage sp = exchange.getIn().getBody(WsaSoapMessage.class);
		logger.debug("Received Answer Message: " + sp.toString());
		OperationHandlerFactory.getOperationHandler().onAnswerReceived(sp);
	}

}

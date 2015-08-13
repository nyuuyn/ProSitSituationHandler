
package situationHandling.workflowOperations;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import utils.soap.WsaSoapMessage;

/**
 * The Class OperationHandlerEndpoint implements the endpoint for the camel
 * route that is used to receive requests from workflows and answers from other
 * workflows/web services.
 *
 * @author Stefan
 */
public class OperationHandlerEndpoint {

    /** The logger for this class. */
    private final static Logger logger = Logger.getLogger(OperationHandlerEndpoint.class);

    /**
     * Receive a request from a workflow. The request should be the body of the
     * exchange.
     *
     * @param exchange
     *            the exchange
     */
    public void receiveRequest(Exchange exchange) {
	WsaSoapMessage wsaSoapMessage = exchange.getIn().getBody(WsaSoapMessage.class);

	logger.debug("Received request:\n" + wsaSoapMessage.toString());

	OperationHandlerFactory.getOperationHandler().handleOperation(wsaSoapMessage);
    }

    /**
     * Receive an answer to a request. The answer should be contained in the
     * body of the exchange.
     *
     * @param exchange
     *            the exchange
     */
    public void receiveAnswer(Exchange exchange) {
	WsaSoapMessage sp = exchange.getIn().getBody(WsaSoapMessage.class);
	logger.debug("Received Answer Message: " + sp.toString());
	OperationHandlerFactory.getOperationHandler().onAnswerReceived(sp);
    }

}

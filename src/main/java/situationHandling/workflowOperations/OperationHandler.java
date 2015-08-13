package situationHandling.workflowOperations;

import situationHandling.storage.datatypes.Situation;
import utils.soap.WsaSoapMessage;

/**
 * The Interface OperationHandler exposes the functionality of the operation
 * handling component to other components. It is the only way other components
 * can and should use the Operation Handler.
 * <p>
 * Instances can be obtained using the {@link OperationHandlerFactory}.
 */
public interface OperationHandler {

    /**
     * Handles a workflow operation, i.e. sends it to a registered endpoint,
     * based on the current situations. The input must be a valid soap message,
     * that uses WS-Addressing to define an id of the message and a reply
     * address.
     * <p>
     * If this is not the case or another error occurs, a fault message will be
     * sent to the reply address.
     *
     * @param wsaSoapMessage
     *            the soap message
     */
    public void handleOperation(WsaSoapMessage wsaSoapMessage);

    /**
     * Notifies the operation handling component about situation changes. This
     * might trigger a rollback of an endpoint that is currently running a
     * operation.
     *
     * @param situation
     *            the situation that changed
     * @param state
     *            the new state of the situation, i.e. true when the situation
     *            appeared or false when it disappeared.
     */
    public void situationChanged(Situation situation, boolean state);

    /**
     * Handles the answer of an endpoint for a workflow request. The Answer will
     * be forwarded to the workflow that requested the operation.
     *
     * @param wsaSoapMessage
     *            A valid soap message the contains the answer to a request. The
     *            answer must use WS-Addressing to relate to the request.
     */
    public void onAnswerReceived(WsaSoapMessage wsaSoapMessage);

}

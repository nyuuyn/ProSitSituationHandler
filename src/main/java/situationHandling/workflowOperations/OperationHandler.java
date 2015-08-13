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
     * Situation changed.
     *
     * @param situation
     *            the situation
     * @param state
     *            the state
     */
    public void situationChanged(Situation situation, boolean state);

    /**
     * On answer received.
     *
     * @param wsaSoapMessage
     *            the wsa soap message
     */
    public void onAnswerReceived(WsaSoapMessage wsaSoapMessage);

}

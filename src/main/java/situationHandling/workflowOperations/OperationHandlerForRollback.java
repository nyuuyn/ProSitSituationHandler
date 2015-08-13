package situationHandling.workflowOperations;

import utils.soap.WsaSoapMessage;

interface OperationHandlerForRollback extends OperationHandler {
    /**
     * Does the same than
     * {@code OperationHandler#handleOperation(WsaSoapMessage)}.
     * <p>
     * Furthermore, this mehtod allows the specification of a rollback handler,
     * in case an operation is not handled the first time. If this is the case
     * an operation handler must be provided to guarantee appropriate rollback
     * handling.
     * 
     * 
     * @param wsaSoapMessage
     * @param rollbackHandler
     */
    public void handleOperation(WsaSoapMessage wsaSoapMessage, RollbackHandler rollbackHandler);
}

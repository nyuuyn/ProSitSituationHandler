package situationHandling.workflowOperations;

import utils.soap.WsaSoapMessage;

/**
 * This interface extends {@link OperationHandler} and provides additional
 * methods to handle operations after rollbacks.
 * 
 * 
 * @author Stefan
 *
 */
interface OperationHandlerForRollback extends OperationHandler {

    /**
     * Does the same than
     * {@code OperationHandler#handleOperation(WsaSoapMessage)}.
     * <p>
     * Furthermore, this method allows the specification of a rollback handler,
     * in case an operation is not handled the first time. If this is the case
     * an operation handler must be provided to guarantee appropriate rollback
     * handling.
     * 
     * 
     * @param wsaSoapMessage
     *            a valid soap request.
     * @param rollbackHandler
     *            the rollback handler that was responsible for the last
     *            rollback.
     * @param checkOnlyAvailable
     *            determines which endpoints are considered: if true, only
     *            available endpoints are used, if false only archived endpoints
     *            are used. Always use true, except when the endpoint selection
     *            already failed one time.
     */
    public void handleOperation(WsaSoapMessage wsaSoapMessage, RollbackHandler rollbackHandler,
	    boolean checkOnlyAvailable);
}

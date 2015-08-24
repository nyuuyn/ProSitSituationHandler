package situationHandling.workflowOperations;

/**
 * A factory for creating OperationHandler objects. Use this factory to get
 * access to the functionality of the operation handling component.
 *
 * @author Stefan
 */
public class OperationHandlerFactory {

    /** The rollback manager used for handling rollbacks. */
    private static RollbackManager rollbackManager = new RollbackManager();

    /**
     * Gets an instance of the operation handling component.
     *
     * @return the operation handler
     */
    public static OperationHandler getOperationHandler() {
	return new OperationHandlerImpl(rollbackManager);
    }

    /**
     * Gets an instance of the operation handling component. Mustbe used after a
     * rollback to handle the operation again.
     * 
     * @return the operation handler
     */
    static OperationHandlerForRollback getOperationHandlerWithRollback() {
	return new OperationHandlerImpl(rollbackManager);
    }

    /**
     * Does the cleanup for the Operation Handling Component to allow a graceful
     * shutdown.
     * 
     */
    public static void shutdown() {
	MessageRouter.shutdown();
    }

}

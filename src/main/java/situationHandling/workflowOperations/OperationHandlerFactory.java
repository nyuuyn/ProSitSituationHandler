/**
 * 
 */
package situationHandling.workflowOperations;


// TODO: Auto-generated Javadoc
/**
 * A factory for creating OperationHandler objects.
 *
 * @author Stefan
 */
public class OperationHandlerFactory {


	
	/** The rollback manager. */
	private static RollbackManager rollbackManager = new RollbackManager();

	/**
	 * Gets the operation handler.
	 *
	 * @return the operation handler
	 */
	public static OperationHandler getOperationHandler() {
		return new OperationHandlerImpl(rollbackManager);
	}
	
	static OperationHandlerForRollback getOperationHandlerWithRollback(){
	    return new OperationHandlerImpl(rollbackManager);
	}

}

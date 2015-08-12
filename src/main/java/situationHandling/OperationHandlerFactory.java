/**
 * 
 */
package situationHandling;


/**
 * @author Stefan
 *
 */
public class OperationHandlerFactory {


	
	private static RollbackManager rollbackManager = new RollbackManager();

	public static OperationHandler getOperationHandler() {
		return new OperationHandlerImpl(rollbackManager);
	}

}

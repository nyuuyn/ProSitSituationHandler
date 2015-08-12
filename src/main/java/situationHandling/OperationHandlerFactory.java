/**
 * 
 */
package situationHandling;

import java.util.HashMap;
import java.util.LinkedList;

import situationHandling.storage.datatypes.Situation;

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

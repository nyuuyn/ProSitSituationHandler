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

	private static HashMap<Situation, LinkedList<RollbackHandler>> rollbackHandlers = new HashMap<>();
	private static HashMap<String, RollbackHandler> runningRollbacks = new HashMap<>();

	public static OperationHandler getOperationHandler() {
		return new OperationHandlerImpl(rollbackHandlers, runningRollbacks);
	}

}

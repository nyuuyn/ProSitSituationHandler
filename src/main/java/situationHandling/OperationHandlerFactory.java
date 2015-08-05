/**
 * 
 */
package situationHandling;

/**
 * @author Stefan
 *
 */
public class OperationHandlerFactory {
	
	
	public static OperationHandler getOperationHandler(){
		return new OperationHandlerImpl();
	}

}

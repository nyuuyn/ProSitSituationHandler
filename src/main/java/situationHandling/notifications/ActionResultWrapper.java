package situationHandling.notifications;

import java.util.Map;
import java.util.concurrent.Future;

import situationHandling.storage.datatypes.Action;

/**
 * A wrapper class that contains the result of the execution of an action and
 * the action itself.
 * 
 * @author Stefan
 *
 */
class ActionResultWrapper {

    /**
     * The action that was executed.
     */
    private Action action;

    /** The result of the execution. */
    private Future<Map<String, String>> result;

    /**
     * Instantiates a new action result wrapper.
     *
     * @param action
     *            the action that was executed.
     * @param result
     *            the result of the execution.
     */
    ActionResultWrapper(Action action, Future<Map<String, String>> result) {
	this.action = action;
	this.result = result;
    }

    /**
     * Gets the action that was executed.
     *
     * @return the action
     */
    Action getAction() {
	return action;
    }

    /**
     * Gets the result of the execution..
     *
     * @return the result
     */
    Future<Map<String, String>> getResult() {
	return result;
    }

}

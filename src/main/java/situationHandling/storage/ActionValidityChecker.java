package situationHandling.storage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pluginManagement.PluginManagerFactory;
import situationHandling.exceptions.InvalidActionException;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Action.ExecutionTime;

/**
 * The Class ActionValidityChecker is used to do semantic checks on an action.
 * <br>
 * 
 * Semantic checks refer to the Parameters of an action and the used plugin. It
 * is checked if:
 * <ol>
 * <li>The plugin exists</li>
 * <li>The right number of params is used</li>
 * <li>The correct params are used</li>
 * <li>The execution time is set</li>
 * </ol>
 * 
 * Uses the {@link InvalidActionException} to signalize constraint violations.
 * 
 * @see Action
 */
class ActionValidityChecker {

	/** The params of the action. */
	private Map<String, String> params;

	/** The plugin id. */
	private String pluginId;

	/**
	 * The execution time.
	 */
	private ExecutionTime executionTime;

	/**
	 * Instantiates a new action validity checker. Checks the params and the
	 * plugin, see class description.
	 *
	 * @param params
	 *            the params
	 * @param pluginId
	 *            the plugin id
	 */
	ActionValidityChecker(Map<String, String> params, String pluginId, ExecutionTime executionTime) {
		this.params = params;
		this.pluginId = pluginId;
		this.executionTime = executionTime;
	}

	/**
	 * Checks the plugin id, i.e. if the plugin exists.
	 *
	 * @throws InvalidActionException
	 *             when the plugin not exists
	 */
	private void checkPluginId() throws InvalidActionException {
		if (!PluginManagerFactory.getPluginManager().pluginExists(pluginId)) {
			throw new InvalidActionException("Plugin with ID " + pluginId + " does not exist");
		}
	}

	/**
	 * Check parameters, i.e. the correct number of params and wheter the right
	 * params are used.
	 *
	 * @throws InvalidActionException
	 *             when there is something wrong with the parameters
	 */
	private void checkParameters() throws InvalidActionException {
		Set<String> allValidParams = PluginManagerFactory.getPluginManager().getPluginParamDescriptions(pluginId);

		//no params required and none submitted
		if (allValidParams == null && (params == null || params.size() == 0)) {
			return;
		}

		// check if params were submitted, but none specified by plugin
		if (allValidParams == null && params != null && params.size() > 0) {
			throw new InvalidActionException("Action specified Params where no params are allowed.");
		}

		// check if too much params were specified by the action
		if (allValidParams.size() < params.size()) {
			HashSet<String> temp = new HashSet<>();
			temp.addAll(params.keySet());
			temp.removeAll(allValidParams);
			throw new InvalidActionException("Unknown Params: " + temp.toString());
		}

		// check if parameters in action contain all params specified by the
		// plugin
		for (String param : allValidParams) {
			if (!params.containsKey(param)) {
				throw new InvalidActionException("Action does not specifiy parameter " + param + ".");
			}
		}
	}

	/**
	 * Checks if the execution time is properly set.
	 * 
	 * @throws InvalidActionException
	 */
	private void checkExecutionTime() throws InvalidActionException {
		if (executionTime == null) {
			throw new InvalidActionException("Action does not specify execution time.");
		}
	}

	/**
	 * Checks the action. See the class description to see wich checks are
	 * performed
	 * 
	 * @param update
	 *            use true if the check is performed before an update of an
	 *            existing action. Use false if the check is performed on a new
	 *            action.
	 * @param the
	 *            id of the action if an update is done. When checking a new
	 *            action this value can be null.
	 *
	 * @throws InvalidActionException
	 *             if one of the defined constraints is violated.
	 */
	public void checkAction(boolean update, Integer actionID) throws InvalidActionException {
		if (update) {// check only specified params

			if (pluginId != null) {
				checkPluginId();
			}

			if (params != null) {
				if (pluginId == null) {
					// no update on plugin. We have to load
					// the plugin from the existing action to be able to check
					// the params
					pluginId = StorageAccessFactory.getRuleStorageAccess().getActionByID(actionID).getPluginID();

				}
				checkParameters();
			}
		} else {// do full check
			checkExecutionTime();
			checkPluginId();
			checkParameters();

		}

	}

}

package situationHandling.storage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pluginManagement.PluginManagerFactory;
import situationHandling.exceptions.InvalidActionException;

class ActionValidityChecker {

	private Map<String, String> params;
	private String pluginId;

	ActionValidityChecker(Map<String, String> params, String pluginId) {
		super();
		this.params = params;
		this.pluginId = pluginId;
	}

	private void checkPluginId() throws InvalidActionException {
		if (!PluginManagerFactory.getPluginManager().pluginExists(pluginId)) {
			throw new InvalidActionException("Plugin with ID " + pluginId
					+ " does not exist");
		}
	}

	private void checkParameters() throws InvalidActionException {
		Set<String> allValidParams = PluginManagerFactory.getPluginManager()
				.getPluginParamDescriptions(pluginId);
		//check if too much params were specified by the action
		if (allValidParams.size() < params.size()){
			HashSet<String> temp = new HashSet<>();
			temp.addAll(params.keySet());
			temp.removeAll(allValidParams);
			throw new InvalidActionException(
					"Unknown Params: " + temp.toString());
		}
		
		//check if parameters in action contain all params specified by the plugin
		for (String param : allValidParams) {
			if (!params.containsKey(param)) {
				throw new InvalidActionException(
						"Action does not specifiy parameter " + param + ".");
			}
		}
	}

	
	public void checkAction() throws InvalidActionException{
		checkPluginId();
		checkParameters();
	}
}

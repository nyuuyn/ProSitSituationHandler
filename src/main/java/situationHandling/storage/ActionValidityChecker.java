package situationHandling.storage;

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
		Set<String> allParams = PluginManagerFactory.getPluginManager()
				.getPluginParamDescriptions(pluginId);
		for (String param : allParams) {
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

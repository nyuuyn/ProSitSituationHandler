package pluginManagement;

import java.util.Set;

import situationHandler.plugin.PluginParams;



class PluginManagerImpl implements PluginManager {
	
	private static final PluginLoader pluginLoader = new PluginLoader();
	
	 public PluginManagerImpl() {
		
	}

	@Override
	public Set<String> getAllPluginIDs() {
	
		return pluginLoader.getPluginIDs();
	}

	@Override
	public String getPluginName(String pluginID) {
		return pluginLoader.getPluginByID(pluginID).getName();
	}

	@Override
	public int getPluginNoOfRequiredParams(String pluginID) {
		return pluginLoader.getPluginByID(pluginID).getNoOfRequiredParams();
	}

	@Override
	public Set<String> getPluginParamDescriptions(String pluginID) {
		return pluginLoader.getPluginByID(pluginID).getParamDescriptions();
	}

	@Override
	public Runnable getPluginSender(String pluginID, String endpoint,
			String message, PluginParams pluginParams) {
		return pluginLoader.getPluginByID(pluginID).getSender(endpoint, message, pluginParams);
	}
	

	@Override
	public void addPlugin() {
		pluginLoader.reload();

	}

	@Override
	public void removePlugin() {
		// TODO Auto-generated method stub

	}



}

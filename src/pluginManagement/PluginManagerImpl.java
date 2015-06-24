package pluginManagement;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

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
	public Callable<Map<String,String>> getPluginSender(String pluginID, String address,
			String message, PluginParams pluginParams) {
		return pluginLoader.getPluginByID(pluginID).getSender(address, message, pluginParams);
	}
	

	@Override
	public void addPlugin(String ID, String path) {
		pluginLoader.addPlugin(ID, path);

	}

	@Override
	public void removePlugin(String ID) {
		pluginLoader.removePlugin(ID);

	}



}

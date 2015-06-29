package pluginManagement;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import situationHandler.plugin.PluginParams;



// TODO: Auto-generated Javadoc
/**
 * The Class PluginManagerImpl.
 */
class PluginManagerImpl implements PluginManager {
	
	/** The Constant pluginLoader. */
	private static final PluginLoader pluginLoader = new PluginLoader();
	
	 /**
 	 * Instantiates a new plugin manager impl.
 	 */
 	public PluginManagerImpl() {
		
	}

	/* (non-Javadoc)
	 * @see pluginManagement.PluginManager#getAllPluginIDs()
	 */
	@Override
	public Set<String> getAllPluginIDs() {
	
		return pluginLoader.getPluginIDs();
	}

	/* (non-Javadoc)
	 * @see pluginManagement.PluginManager#getPluginName(java.lang.String)
	 */
	@Override
	public String getPluginName(String pluginID) {
		return pluginLoader.getPluginByID(pluginID).getName();
	}

	/* (non-Javadoc)
	 * @see pluginManagement.PluginManager#getPluginNoOfRequiredParams(java.lang.String)
	 */
	@Override
	public int getPluginNoOfRequiredParams(String pluginID) {
		return pluginLoader.getPluginByID(pluginID).getNoOfRequiredParams();
	}

	/* (non-Javadoc)
	 * @see pluginManagement.PluginManager#getPluginParamDescriptions(java.lang.String)
	 */
	@Override
	public Set<String> getPluginParamDescriptions(String pluginID) {
		return pluginLoader.getPluginByID(pluginID).getParamDescriptions();
	}

	/* (non-Javadoc)
	 * @see pluginManagement.PluginManager#getPluginSender(java.lang.String, java.lang.String, java.lang.String, situationHandler.plugin.PluginParams)
	 */
	@Override
	public Callable<Map<String,String>> getPluginSender(String pluginID, String address,
			String message, PluginParams pluginParams) {
		return pluginLoader.getPluginByID(pluginID).getSender(address, message, pluginParams);
	}
	

	/* (non-Javadoc)
	 * @see pluginManagement.PluginManager#addPlugin(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean addPlugin(String ID, String path) {
		return pluginLoader.addPlugin(ID, path);

	}

	/* (non-Javadoc)
	 * @see pluginManagement.PluginManager#removePlugin(java.lang.String)
	 */
	@Override
	public boolean removePlugin(String ID) {
		return pluginLoader.removePlugin(ID);

	}



}

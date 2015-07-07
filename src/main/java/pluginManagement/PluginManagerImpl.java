package pluginManagement;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import situationHandler.plugin.Plugin;
import situationHandler.plugin.PluginParams;

/**
 * The Class PluginManagerImpl is the default implementation for
 * {@link PluginManager}. Its main task is to use the PluginLoader to access the
 * currently loaded Plugins.
 */
class PluginManagerImpl implements PluginManager {

	/** The pluginLoader. */
	private static final PluginLoader pluginLoader = new PluginLoader();

	/**
	 * Instantiates a new plugin manager impl.
	 */
	public PluginManagerImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pluginManagement.PluginManager#getAllPluginIDs()
	 */
	@Override
	public Set<String> getAllPluginIDs() {

		return pluginLoader.getPluginIDs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pluginManagement.PluginManager#getPluginName(java.lang.String)
	 */
	@Override
	public String getPluginName(String pluginID) {
		return pluginLoader.getPluginByID(pluginID).getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pluginManagement.PluginManager#getPluginNoOfRequiredParams(java.lang.
	 * String)
	 */
	@Override
	public int getPluginNoOfRequiredParams(String pluginID) {
		return pluginLoader.getPluginByID(pluginID).getNoOfRequiredParams();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pluginManagement.PluginManager#getPluginParamDescriptions(java.lang.String
	 * )
	 */
	@Override
	public Set<String> getPluginParamDescriptions(String pluginID) {
		return pluginLoader.getPluginByID(pluginID).getParamDescriptions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pluginManagement.PluginManager#getPluginSender(java.lang.String,
	 * java.lang.String, java.lang.String, situationHandler.plugin.PluginParams)
	 */
	@Override
	public Callable<Map<String, String>> getPluginSender(String pluginID,
			String address, String payload, PluginParams pluginParams) {
		return pluginLoader.getPluginByID(pluginID).getSender(address, payload,
				pluginParams);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pluginManagement.PluginManager#addPlugin(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean addPlugin(String ID, String path) {
		return pluginLoader.addPlugin(ID, path);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pluginManagement.PluginManager#removePlugin(java.lang.String)
	 */
	@Override
	public boolean removePlugin(String ID) {
		return pluginLoader.removePlugin(ID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pluginManagement.PluginManager#getPluginInformation(java.lang.String)
	 */
	@Override
	public PluginInfo getPluginInformation(String pluginID) {
		Plugin p = pluginLoader.getPluginByID(pluginID);
		PluginInfo pluginInfo = null;
		if (p != null) {
			pluginInfo = new PluginInfo(p.getID(), p.getName(),
					p.getNoOfRequiredParams(), p.getParamDescriptions());
		}
		return pluginInfo;
	}

}

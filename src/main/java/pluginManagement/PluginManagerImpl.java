package pluginManagement;

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import situationHandler.plugin.Plugin;
import situationHandler.plugin.PluginParams;

/**
 * The Class PluginManagerImpl is the default implementation for
 * {@link PluginManager}. Its main task is to use the PluginLoader to access the
 * currently loaded Plugins.
 */
class PluginManagerImpl implements PluginManager {

    /** The pluginLoader. */
    private PluginLoader pluginLoader;

    /** The logger for this class. */
    private final static Logger logger = Logger.getLogger(PluginManagerImpl.class);

    /**
     * Instantiates a new plugin manager impl.
     */
    public PluginManagerImpl(PluginLoader pluginLoader) {
	this.pluginLoader = pluginLoader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pluginManagement.PluginManager#getAllPluginIDs()
     */
    @Override
    public Set<String> getAllPluginIDs() {
	logger.debug("Getting all Plugin IDs");
	return pluginLoader.getPluginIDs();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pluginManagement.PluginManager#getPluginName(java.lang.String)
     */
    @Override
    public String getPluginName(String pluginID) {
	logger.trace("Getting plugin name.");
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
	logger.trace("Getting required Parameters");
	return pluginLoader.getPluginByID(pluginID).getNoOfRequiredParams();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pluginManagement.PluginManager#getPluginParamDescriptions(java.lang.
     * String )
     */
    @Override
    public Set<String> getPluginParamDescriptions(String pluginID) {
	logger.trace("Getting param Descriptions");
	return pluginLoader.getPluginByID(pluginID).getParamDescriptions();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pluginManagement.PluginManager#getPluginSender(java.lang.String,
     * java.lang.String, java.lang.String, situationHandler.plugin.PluginParams)
     */
    @Override
    public Callable<Map<String, String>> getPluginSender(String pluginID, String address,
	    String payload, PluginParams pluginParams) {
	logger.trace("Getting Sender for:" + pluginID);
	Plugin plugin = pluginLoader.getPluginByID(pluginID);
	if (plugin != null) {
	    return plugin.getSender(address, payload, pluginParams);
	} else {
	    return null;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see pluginManagement.PluginManager#addPlugin(java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean addPlugin(String ID, String path, boolean deleteJar) {
	logger.debug("Adding plugin: " + ID);
	return pluginLoader.addPlugin(ID, path, deleteJar);

    }

    /*
     * (non-Javadoc)
     * 
     * @see pluginManagement.PluginManager#removePlugin(java.lang.String)
     */
    @Override
    public boolean removePlugin(String ID) {
	logger.debug("Removing Plugin: " + ID);
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
	logger.debug("Getting information about plugin: " + pluginID);
	Plugin p = pluginLoader.getPluginByID(pluginID);
	PluginInfo pluginInfo = null;
	if (p != null) {
	    pluginInfo = new PluginInfo(p.getID(), p.getName(), p.getNoOfRequiredParams(),
		    p.getParamDescriptions());
	}
	return pluginInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pluginManagement.PluginManager#pluginExists(java.lang.String)
     */
    @Override
    public boolean pluginExists(String Id) {
	return (pluginLoader.getPluginByID(Id) != null);
    }

    @Override
    public URL getPluginManual(String Id) {
	if (pluginExists(Id)) {
	    return pluginLoader.getPluginByID(Id).getManual();
	} else {
	    return null;
	}
    }

    @Override
    public void shutdownPluginManagement() {
	pluginLoader.shutdown();
    }

}

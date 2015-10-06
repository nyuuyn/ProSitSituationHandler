package pluginManagement;

import main.SituationHandlerProperties;

/**
 * A factory for creating and initializing PluginManager objects.
 * 
 * @see PluginManager
 */
public class PluginManagerFactory {

    /** The pluginLoader used for the default Plugin Managers. */
    private static final PluginLoader pluginLoader = new PluginLoader(
	    SituationHandlerProperties.getPluginStartupFolder(),
	    SituationHandlerProperties.getPluginRuntimeFolder());

    /**
     *
     * @return an instance of plugin manager.
     */
    public static PluginManager getPluginManager() {
	return new PluginManagerImpl(pluginLoader);
    }

    /**
     * Shuts down the plugin functionality. After shuting down, the plugins
     * cannot be used anymore (and there is no way to restart). *
     * <p>
     * Equal to {@link PluginManager#shutdownPluginManagement()}
     */
    public static void shutdownPluginManagement() {
	getPluginManager().shutdownPluginManagement();
    }

}

package pluginManagement;

/**
 * A factory for creating and initializing PluginManager objects.
 * 
 * @see PluginManager
 */
public class PluginManagerFactory {
	
	/**
	 * The path to the plugin folder. Jar-Files in this folder are loaded at
	 * startup.
	 */
	private static final String PLUGIN_FOLDER = "plugins";

	/**
	 * The path to the runtime folder. This folder is used to store jars that
	 * are added at runtime.
	 */
	private static final String RUNTIME_FOLDER = "runtime";
	

	/** The pluginLoader used for the default Plugin Managers. */
	private static final PluginLoader pluginLoader = new PluginLoader(PLUGIN_FOLDER, RUNTIME_FOLDER);

	/**
	 *
	 * @return an instance of plugin manager.
	 */
	public static PluginManager getPluginManager() {
		return new PluginManagerImpl(pluginLoader);
	}

}

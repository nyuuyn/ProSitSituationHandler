package pluginManagement;

/**
 * A factory for creating PluginManager objects.
 * 
 * @see PluginManager
 */
public class PluginManagerFactory {

	/**
	 *
	 * @return an instance of plugin manager.
	 */
	public static PluginManager getPluginManager() {
		return new PluginManagerImpl();
	}

}

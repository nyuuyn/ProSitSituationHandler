package pluginManagement;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating PluginManager objects.
 */
public class PluginManagerFactory {
	
	/**
	 * Gets the plugin manager.
	 *
	 * @return the plugin manager
	 */
	public static PluginManager getPluginManager(){
		return new PluginManagerImpl();
	}

}

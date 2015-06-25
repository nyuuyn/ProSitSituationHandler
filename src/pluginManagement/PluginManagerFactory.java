package pluginManagement;

public class PluginManagerFactory {
	
	public static PluginManager getPluginManager(){
		return new PluginManagerImpl();
	}

}

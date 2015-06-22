package pluginManagement;

public class PluginManagerFactory {
	
	public static PluginManager getManager(){
		return new PluginManagerImpl();
	}

}

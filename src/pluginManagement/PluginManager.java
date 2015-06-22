package pluginManagement;

public interface PluginManager {

	// TOOD: Params und Return Werte

	public void getAllPluginNames();
	
	//Plugin Information, wie die Plugin params usw
	public void getPluginInformation();

	// dient praktisch als Factory fuer die Plugins --> liefert Instanziierung
	// des Plugins mit dem entsprechenden Namen
	public void getPlugin(String pluginName);

	public void addPlugin();

	public void removePlugin();

}

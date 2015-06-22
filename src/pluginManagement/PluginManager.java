package pluginManagement;

import java.util.Set;

import situationHandler.plugin.PluginParams;

public interface PluginManager {

	// TOOD: Params und Return Werte

	public Set<String> getAllPluginIDs();
	
	//Plugin Information, wie die Plugin params usw
	public String getPluginName(String pluginID);

	public int getPluginNoOfRequiredParams(String pluginID);
	
	public Set<String> getPluginParamDescriptions(String pluginID);
	
	// dient praktisch als Factory fuer die Plugins --> liefert Instanziierung
	// des Plugins mit dem entsprechenden Namen
	public Runnable getPluginSender(String pluginID, String address, String message, PluginParams pluginParams);

	public void addPlugin();

	public void removePlugin();

}

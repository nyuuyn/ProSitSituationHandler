package pluginManagement;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import situationHandler.plugin.PluginParams;

public interface PluginManager {

	// TOOD: Params und Return Werte

	public Set<String> getAllPluginIDs();

	// Plugin Information, wie die Plugin params usw
	public String getPluginName(String pluginID);

	public int getPluginNoOfRequiredParams(String pluginID);

	public Set<String> getPluginParamDescriptions(String pluginID);

	// dient praktisch als Factory fuer die Plugins --> liefert Instanziierung
	// des Plugins mit dem entsprechenden Namen
	public Callable<Map<String, String>> getPluginSender(String pluginID,
			String address, String message, PluginParams pluginParams);

	public void addPlugin(String ID, String path);

	public void removePlugin(String ID);

}

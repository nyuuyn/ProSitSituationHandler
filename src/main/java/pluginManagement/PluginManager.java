package pluginManagement;

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import situationHandler.plugin.Plugin;
import situationHandler.plugin.PluginParams;

/**
 * The Interface PluginManager gives access to all releveant functionality of
 * the plugin management functionality. The Plugin Manager allows to add and
 * remove Plugins at runtime. Furthermore, it is used to access the loaded
 * Plugins.
 * <p>
 * Each Plugin must implement the Interface
 * {@link situationHandler.plugin.Plugin}. A Plugin provides the following
 * information:
 * <ul>
 * <li>Plugin ID - A unique id that identifies the plugin. For example:
 * com.example.SamplePlugin
 * <li>Plugin Name - The name of the plugin. Can be chosen freely and should
 * provide nice readability.
 * <li>Number of Required Params - How many optional params the plugin needs.
 * Each plugin can specify an arbitrary number of optional Params that it needs.
 * <li>Descriptions of the optional Params. The Description is also used as
 * identifier for a parameter when passing the paramters to the plugin.
 * </ul>
 * 
 * Furthermore, a Plugin provides a method to create a Callable. The callable
 * implements the main functionality of a plugin, i.e. a communication task. The
 * return value of the Callable can be used to give the use of the Plugin an
 * answer. However, the plugin is free to send an answer back to the initial
 * sender itself.
 * <p>
 * To access a plugin, in most cases the id is required.
 * 
 * 
 * @see Plugin
 */
public interface PluginManager {

	/**
	 * Gets the ids of all plugins that are currently loaded. The id can be used
	 * to access a certain plugin.
	 *
	 * @return all ids as strings
	 */
	public Set<String> getAllPluginIDs();

	/**
	 * Gets all information about a plugin in a wrapper class.
	 * 
	 * @param pluginID
	 *            the plugin to get information about
	 * @return An instance of {@link PluginInfo}, that contains all information
	 *         about the specified Plugin. Null, if no plugin with this ID
	 *         exists.
	 */
	public PluginInfo getPluginInformation(String pluginID);

	/**
	 * Gets the name of the plugin that is registred under the specified ID.
	 *
	 * @param pluginID
	 *            the id of the plugin to look up
	 * @return the plugin name
	 */
	public String getPluginName(String pluginID);

	/**
	 * Gets the number of params that a plugin requires.
	 *
	 * @param pluginID
	 *            the id of the plugin to look up
	 * @return the number of required params. Null if no plugin with the
	 *         specified ID was found.
	 */
	public int getPluginNoOfRequiredParams(String pluginID);

	/**
	 * Gets the descriptions of the optional params of the plugin. The
	 * Description is also used as identifier for a parameter when passing the
	 * paramters to the plugin.
	 *
	 * @param pluginID
	 *            the plugin id
	 * @return the plugin param descriptions. Null if no plugin with the
	 *         specified ID was found.
	 */
	public Set<String> getPluginParamDescriptions(String pluginID);

	/**
	 * Gets the specified plugin's implementation as {@link Callable}. To
	 * execute the plugin, use this method to get a callable and run it. IF the
	 * plugin provides an answer, it is wrapped in the return value of the
	 * callable. See the documenation of the plugin to get information about
	 * possible return values.
	 *
	 * @param pluginID
	 *            the id of the plugin that should be used
	 * @param address
	 *            the address of the receiver, this instance of the plugin
	 *            should use
	 * @param payload
	 *            the payload to send
	 * @param pluginParams
	 *            the optional parameters. See the documentation of the plugin
	 *            for information about them. Use the Parameter Description as
	 *            name for the param and an arbitrary string as value.
	 * @return an instance of callable, that executes some functionality
	 *         specified by the plugin. Null if no plugin with the specified ID
	 *         was found.
	 */
	public Callable<Map<String, String>> getPluginSender(String pluginID, String address, String payload,
			PluginParams pluginParams);

	/**
	 * Adds a plugin at runtime, using {@code ID} as ID for the plugin. The
	 * Plugin is loaded from the specified path.
	 *
	 * @param ID
	 *            the id of the plugin
	 * @param path
	 *            the path the plugin is loaded from
	 * @param deleteJar
	 *            if true, the file at {@code path} is deleted after loading the
	 *            jar
	 * @return true, if successful
	 */
	public boolean addPlugin(String ID, String path, boolean deleteJar);

	/**
	 * Removes the plugin. The plugin is not longer available for use after
	 * using this method.
	 *
	 * @param ID
	 *            the id of the plugin to remove
	 * @return true, if successful, false if no plugin with this id exists.
	 */
	public boolean removePlugin(String ID);

	/**
	 * 
	 * Checks wheter a plugin exists, i.e. is registred with the PluginManager.
	 * 
	 * @param Id
	 *            The id of the plugin to check
	 * @return true, if plugin exists, false else
	 */
	public boolean pluginExists(String Id);

	/**
	 * Gets the plugin's manual.
	 * 
	 * @param Id
	 *            the id of the plugin
	 * @returnAn URL to the file that contains the manual of the plugin. The
	 *           manual can be plain text or html.
	 */
	public URL getPluginManual(String Id);
	
	/**
	 * Shuts down the plugin functionality. After shuting down, the plugins
	 * cannot be used anymore (and there is no way to restart).
	 * <p>
	 * Equal to {@link PluginManagerFactory#shutdownPluginManagement()}
	 */
	public void shutdownPluginManagement();

}

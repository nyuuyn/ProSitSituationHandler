package pluginManagement;

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
	 * @param message
	 *            the message to send
	 * @param pluginParams
	 *            the optional parameters. See the documentation of the plugin
	 *            for information about them. Use the Parameter Description as
	 *            name for the param and an arbitrary string as value.
	 * @return an instance of callable, that executes some functionality
	 *         specified by the plugin. Null if no plugin with the specified ID
	 *         was found.
	 */
	public Callable<Map<String, String>> getPluginSender(String pluginID,
			String address, String message, PluginParams pluginParams);

	/**
	 * Adds a plugin at runtime, using {@code ID} as ID for the plugin. The
	 * Plugin is loaded from the specified path.
	 *
	 * @param ID
	 *            the id of the plugin
	 * @param path
	 *            the path the plugin is loaded from
	 * @return true, if successful
	 */
	public boolean addPlugin(String ID, String path);

	/**
	 * Removes the plugin. The plugin is not longer available for use after
	 * using this method.
	 *
	 * @param ID
	 *            the id of the plugin to remove
	 * @return true, if successful
	 */
	public boolean removePlugin(String ID);

}

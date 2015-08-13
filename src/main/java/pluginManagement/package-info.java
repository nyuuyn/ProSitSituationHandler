/**
 * 
 * This class contains classes that are responsible for the management of the
 * plugins.In this case a Plugin is a exchangeable unit that provides a certain
 * type of communication.
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
 * The plugin management functionality provides all functionality to load and
 * run plugins. Furthermore, Plugins can be dynamically added and deleted at
 * runtime.
 * 
 * To make a Plugin available to the application, there are two possibilities.
 * For both possibilities the Plugin must be provided as .jar file:
 * <ol>
 * <li>Put the .jar File in the Plugin Folder of the application. The Plugin
 * will then be loaded when the Situation Handler starts. The jar-File MUST have
 * the same name than its id. For example if the id is
 * "com.example.SamplePlugin", the jar-File has to be named
 * "com.example.SamplePlugin.jar".
 * <li>Use the {@link Plugin Manager} to add a Plugin at runtime. All you have
 * to do is to provide a path to the .jar-File. Note that the plugin will not be
 * available anymore, if you restart the Situation Handler.
 * </ol>
 * 
 * To access the Plugin Management, the
 * {@link pluginManagement.PluginManagerFactory} can be used to create an
 * instance of {@link Plugin Mangager.} The Plugin Manager Interface gives
 * access to all relevant functionality.
 * 
 * @see situationHandler.plugin.Plugin
 * @see pluginManagement.PluginManager
 * 
 */
package pluginManagement;
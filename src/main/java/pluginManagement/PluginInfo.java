package pluginManagement;

import java.util.Set;

import situationHandler.plugin.Plugin;

/**
 * The Class PluginInfo is a wrapper class for plugin information. It contains
 * all relevant information about a plugin. It can be used to easily exchange
 * information about plugins.
 * 
 * For further information see {@link Plugin}
 */
public class PluginInfo {

	/** The id of the plugin. */
	private String id;

	/** The name of the plugin. */
	private String name;

	/**
	 * The no of required params of the plugin. The number of parameters needed
	 * by this plugin.
	 */
	private int noOfRequiredParams;

	/**
	 * The param descriptions. The descriptions of the parameters required by
	 * the plugin. Also used as name for the parameter when handing over the
	 * paramters to the plugin.
	 */
	private Set<String> paramDescriptions;

	/**
	 * Creates a new instance of PluginInfo. Default COnstructor. Use the
	 * setters to init the instance afterwards.
	 */
	public PluginInfo() {
	}

	/**
	 * Creates a new instance of PluginInfo and initialize it.
	 *
	 * @param id
	 *            The id of the plugin
	 * @param name
	 *            the name of the plugin.
	 * @param noOfRequiredParams
	 *            the no of required params of the plugin. The number of
	 *            parameters needed by this plugin.
	 * @param paramDescriptions
	 *            The param descriptions. The descriptions of the parameters
	 *            required by the plugin. Also used as name for the parameter
	 *            when handing over the paramters to the plugin.
	 */
	public PluginInfo(String id, String name, int noOfRequiredParams,
			Set<String> paramDescriptions) {
		this.id = id;
		this.name = name;
		this.noOfRequiredParams = noOfRequiredParams;
		this.paramDescriptions = paramDescriptions;
	}

	/**
	 * Gets the id of the plugin.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the plugin
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the name of the plugin.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the plugin.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the no of required params of the plugin. The number of parameters
	 * needed by this plugin.
	 * 
	 * @return the no of required params
	 */
	public int getNoOfRequiredParams() {
		return noOfRequiredParams;
	}

	/**
	 * Sets he no of required params of the plugin. The number of parameters
	 * needed by this plugin.
	 *
	 * @param noOfRequiredParams
	 *            the new no of required params
	 */
	public void setNoOfRequiredParams(int noOfRequiredParams) {
		this.noOfRequiredParams = noOfRequiredParams;
	}

	/**
	 * Gets the param descriptions. The descriptions of the parameters required
	 * by the plugin. Also used as name for the parameter when handing over the
	 * paramters to the plugin.
	 * 
	 *
	 * @return the param descriptions
	 */
	public Set<String> getParamDescriptions() {
		return paramDescriptions;
	}

	/**
	 * Sets The param descriptions. The descriptions of the parameters required
	 * by the plugin. Also used as name for the parameter when handing over the
	 * paramters to the plugin.
	 * 
	 *
	 * @param paramDescriptions
	 *            the new param descriptions
	 */
	public void setParamDescriptions(Set<String> paramDescriptions) {
		this.paramDescriptions = paramDescriptions;
	}

}

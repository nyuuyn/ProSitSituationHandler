package api.configuration;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import pluginManagement.PluginInfo;
import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.Plugin;

/**
 * The Class PluginAPI implements the functionality of the rest configuration
 * api for the plugins. For each allowed rest-operation, there is a dedicated
 * method.
 * <p>
 * The class serves as target for the camel route that specifies the rest api
 * methods.
 * 
 * @see Plugin
 * @see PluginManager
 */
public class PluginAPI {

	/**
	 * Plugin Manager to access the plugins.
	 */
	private PluginManager pm;

	/**
	 * Creates a new instance of PluginAPI and does necessary configuration.
	 */
	public PluginAPI() {
		pm = PluginManagerFactory.getPluginManager();
	}

	/**
	 * Gets information about all plugins that are currently registred.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param exchange
	 *            the exchange that contains the received message. Also serves
	 *            as container for the answer.
	 * @return The plugin information as list. If there are no plugins, an empty list
	 *         is returned. The return value is stored in the exchange.
	 */
	public void getPlugins(Exchange exchange) {
		List<PluginInfo> pluginInfos = new LinkedList<>();

		pm.getAllPluginIDs().forEach(
				id -> pluginInfos.add(pm.getPluginInformation(id)));

		exchange.getIn().setBody(pluginInfos);
	}

	public void addPlugin(Exchange exchange) {
		System.out.println(exchange.getIn().getBody(InputStream.class));
		
		System.out.println(exchange.getIn().getHeaders());
		
//		exchange.getOut().setBody(exchange.getIn().getBody());

		// X-File-Name=situationHandler.gmail.jar
		
		System.out.println("Adding Plugin");
//		NettyChannelBufferStreamCache asdasd = (NettyChannelBufferStreamCache) exchange.getIn().getBody();

		
//		exchange.getIn().setBody("File accepted.");
//		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	}

	/**
	 * Gets pluginInformation by id.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param pluginID
	 *            the plugin id
	 * 
	 * @param exchange
	 *            the exchange that contains the received message. Also serves
	 *            as container for the answer.
	 * @return The informationabout the plugin. If there is no plugin with this id a 404-error is
	 *         returned. The return value is stored in the exchange.
	 */
	public void getPluginByID(String pluginID, Exchange exchange) {
		PluginInfo pi = pm.getPluginInformation(pluginID);
		if (pi != null) {
			exchange.getIn().setBody(pi);
		} else {
			exchange.getIn().setBody("Plugin " + pluginID + " not found.");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		}
	}

	/**
	 * Deletes the plugin with the given id.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param pluginID
	 *            the plugin id
	 * @param exchange
	 *            the exchange that contains the received message. Also serves
	 *            as container for the answer.
	 * @return A 404-error, if there is no plugin with the given id.
	 */
	public void deletePlugin(String pluginID, Exchange exchange) {
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
		if (pm.removePlugin(pluginID)) {
			exchange.getIn().setBody(
					"Plugin " + pluginID + "successfully deleted.");
		} else {
			exchange.getIn().setBody("Plugin " + pluginID + " not found.");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		}
	}

}

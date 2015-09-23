package restApiImpl;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.commons.io.IOUtils;

import main.CamelUtil;
import pluginManagement.PluginInfo;
import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.Plugin;
import situationHandling.storage.StorageAccessFactory;

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
     * @return The plugin information as list. If there are no plugins, an empty
     *         list is returned. The return value is stored in the exchange.
     */
    public void getPlugins(Exchange exchange) {
	List<PluginInfo> pluginInfos = new LinkedList<>();

	pm.getAllPluginIDs().forEach(id -> pluginInfos.add(pm.getPluginInformation(id)));

	exchange.getIn().setBody(pluginInfos);
    }

    /**
     * Adds a new plugin for use. Takes multipart/form-data as input. The
     * plugin-ID has to be set using the header 'x-file-name'.
     * <p>
     * Target method for a camel route. The exchange is created by camel.
     * 
     * 
     * @param exchange
     *            the exchange that contains the received message. Also serves
     *            as container for the answer.
     * @return A success message or a 422 http error code, when there already
     *         exists a plugin with this id.
     */
    public void addPlugin(Exchange exchange) {
	String pluginID = (String) exchange.getIn().getHeader("x-file-name");
	String directory = "tempfiles";
	String filename = pluginID + ".jar";

	// get first attachment, then save at temporarily (jetty version)
	try {
	    CamelUtil.getProducerTemplate().sendBody("file:" + directory + "?fileName=" + filename,
		    exchange.getIn()
			    .getAttachment(exchange.getIn().getAttachmentNames().iterator().next())
			    .getContent());
	} catch (CamelExecutionException | IOException e) {
	    e.printStackTrace();
	}

	// TODO: finale Komponente festlegen..
	// save file temporarily (netty version)
	// CamelUtil.getProducerTemplate().sendBody(
	// "file:" + directory + "?fileName=" + filename,
	// exchange.getIn().getBody());

	// add plugin (also deletes temp file)
	exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "text/plain; charset=utf-8");
	if (pm.addPlugin(pluginID, directory + "/" + filename, true)) {
	    exchange.getOut().setBody("Upload Complete!");
	} else {
	    exchange.getOut().setBody("Plugin already exists. Plugin was NOT added.");
	    exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
	}

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
     * @return The informationabout the plugin. If there is no plugin with this
     *         id a 404-error is returned. The return value is stored in the
     *         exchange.
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

    public void getPluginManual(String pluginId, Exchange exchange) {
	URL manualURL = pm.getPluginManual(pluginId);

	exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	if (manualURL != null) {
	    try {
		String man = IOUtils.toString(manualURL, "UTF-8");
		man = man.replaceAll("[\\r\\n\\t]", "");
		exchange.getIn().setBody(man);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	} else {
	    exchange.getIn().setBody(
		    "Plugin " + pluginId + " not found or plugin does not provide manual.");
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
     * @param deletePlugins
     *            Specifies whether the plugins should be deleted or not. Should
     *            be "true" or "false".
     * @param exchange
     *            the exchange that contains the received message. Also serves
     *            as container for the answer.
     * @return A 404-error, if there is no plugin with the given id.
     */
    public void deletePlugin(String pluginID, String deletePlugins, Exchange exchange) {
	// do not delete if invalid argument..
	boolean delete = Boolean.parseBoolean(deletePlugins);

	if (pm.removePlugin(pluginID)) {
	    if (delete) {
		StorageAccessFactory.getRuleStorageAccess().deleteActionsByPlugin(pluginID);
	    }
	    exchange.getIn().setBody(
		    new RestAnswer("Plugin successfully deleted.", String.valueOf(pluginID)));
	} else {
	    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	    exchange.getIn().setBody("Plugin " + pluginID + " not found.");
	    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
	}
    }

}

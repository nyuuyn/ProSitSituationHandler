package api.configuration;

import java.util.LinkedList;
import java.util.List;

import org.apache.camel.Exchange;

import pluginManagement.PluginInfo;
import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;

public class PluginAPI {

	private PluginManager pm;

	public PluginAPI() {
		pm = PluginManagerFactory.getPluginManager();
	}

	public void getPlugins(Exchange exchange) {
		List<PluginInfo> pluginInfos = new LinkedList<>();

		pm.getAllPluginIDs().forEach(
				id -> pluginInfos.add(pm.getPluginInformation(id)));

		exchange.getIn().setBody(pluginInfos);
	}

	public void addPlugin(Exchange exchange) {
		// TODO
	}

	public void getPluginByID(Exchange exchange, String pluginID) {
		PluginInfo pi = pm.getPluginInformation(pluginID);
		if (pi != null) {
			exchange.getIn().setBody(pi);
		} else {
			exchange.getIn().setBody("Plugin " + pluginID + " not found.");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		}
	}

	public void deletePlugin(Exchange exchange, String pluginID) {
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

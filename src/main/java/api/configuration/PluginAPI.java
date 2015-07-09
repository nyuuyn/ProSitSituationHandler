package api.configuration;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.swing.plaf.multi.MultiFileChooserUI;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.netty4.NettyPayloadHelper;
import org.apache.camel.component.netty4.http.NettyChannelBufferStreamCache;
import org.eclipse.jetty.servlets.MultiPartFilter;
import org.eclipse.jetty.util.MultiPartInputStream.MultiPart;

import pluginManagement.PluginInfo;
import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import routes.CamelUtil;
import situationHandler.plugin.Plugin;
import situationHandler.plugin.PluginParams;

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

		pm.getAllPluginIDs().forEach(
				id -> pluginInfos.add(pm.getPluginInformation(id)));

		exchange.getIn().setBody(pluginInfos);
	}

	public void addPlugin(Exchange exchange) {

		// // System.out.println();
		//
		// String data = exchange.getIn().getBody(String.class);
		// String contentType = (String)
		// exchange.getIn().getHeader("Content-Type");
		// String boundary =
		// contentType.substring(contentType.lastIndexOf("boundary=")).trim();
		// // String boundary =
		// contentType.substring(contentType.lastIndexOf("boundary=")).replace("-",
		// "").trim();
		// // System.out.println("Boundary: "+boundary);
		//
		//
		// String[] multi = data.split("Content-Disposition");
		//
		//
		//
		// System.out.println("Length: " + multi.length);
		//
		// if (multi.length == 3){
		// //pluginID
		// // System.out.println("Start"+multi[0]);
		//
		// multi[1] = multi[1].replace(boundary, "");
		// System.out.println("Username"+multi[1]);
		//
		// // HttpPostRequestDecoder as = new HttpPostRequestDecoder(request);
		//
		//
		//
		// System.out.println("Hochkomma: " + multi[1].lastIndexOf("\""));
		// System.out.println("Minus: " + multi[1].indexOf("-"));
		//
		// String pluginID = multi[1].substring(multi[1].lastIndexOf("\""),
		// multi[1].length() - boundary.length()).trim();
		// // String pluginID = multi[1].substring(multi[1].lastIndexOf("\""),
		// multi[1].indexOf("-"));
		// System.out.println("ID:" + pluginID);
		// }

		// NettyChannelBufferStreamCache assadasd;
		// MultiPartFilter s;
		// s.

		String pluginID = (String) exchange.getIn().getHeader("x-file-name");
		System.out.println("pluginID: " + pluginID);
		String directory = "tempfiles";
		String filename =  pluginID + ".jar";
		
		CamelUtil.getProducerTemplate().sendBody(
				"file:" + directory + "?fileName=" +filename,
				exchange.getIn().getBody());

		pm.addPlugin(pluginID, directory + "/" + filename, true);
		
		PluginParams params = new PluginParams();

		// mail plugin (deactive anti virus)

			params.setParam("Email Subject", "Gmail Plugin Test");
		
		try {
			pm.getPluginSender(pluginID, "stefan.fuerst.89@gmail.com", "Dies ist ein Test", params).call();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// exchange.getIn().getBody());

		// System.out.println(exchange.getIn().getHeaders());

		// exchange.getOut().setBody(exchange.getIn().getBody());

		// X-File-Name=situationHandler.gmail.jar

		// System.out.println("Adding Plugin");
		// NettyChannelBufferStreamCache asdasd =
		// (NettyChannelBufferStreamCache) exchange.getIn().getBody();

		// exchange.getIn().setHeader(Exchange.CONTENT_TYPE,
		// "text/plain; charset=utf-8");
		exchange.getOut().setHeader(Exchange.CONTENT_TYPE,
				"text/html; charset=utf-8");
		exchange.getOut().setBody("{\"msg\":\"Upload Complete\"}");
		// exchange.getIn().setBody("Upload Complete");

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

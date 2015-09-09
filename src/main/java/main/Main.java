package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import pluginManagement.PluginManagerFactory;
import restApiImpl.EndpointAPI;
import restApiImpl.HistoryAPI;
import restApiImpl.PluginAPI;
import restApiImpl.RestApiRoutes;
import restApiImpl.RuleAPI;
import situationHandling.notifications.NotificationComponentFactory;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.workflowOperations.OperationHandlerEndpoint;
import situationHandling.workflowOperations.OperationHandlerFactory;
import situationManagement.SituationEndpoint;
import situationManagement.SituationManagerFactory;

public class Main {

    private static CamelContext context;

    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
	startShutdownListener();

	context = new DefaultCamelContext();

	// Uncomment this to debug http requests (using fiddler)
	// context.getProperties().put("http.proxyHost", "localhost");
	// context.getProperties().put("http.proxyPort", "8888");

	JndiRegistry registry = context.getRegistry(JndiRegistry.class);

	// register beans for use
	registry.bind("ruleApi", RuleAPI.class);
	registry.bind("endpointApi", EndpointAPI.class);
	registry.bind("pluginApi", PluginAPI.class);
	registry.bind("historyApi", HistoryAPI.class);

	registry.bind("operationHandlerEndpoint", OperationHandlerEndpoint.class);
	registry.bind("situationEndpoint", SituationEndpoint.class);

	// resource handler for serving the web app
	ResourceHandler webapp = new ResourceHandler();

	try {
	    webapp.setBaseResource(Resource.newResource(new File(GlobalProperties.WEB_APP_PATH)));
	} catch (MalformedURLException e1) {
	    e1.printStackTrace();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	registry.bind("webApp", webapp);

	try {
	    // add routes
	    context.addRoutes(
		    new SituationHandlerRouteBuilder("0.0.0.0", GlobalProperties.NETWORK_PORT));

	    context.addRoutes(new RestApiRoutes("0.0.0.0", GlobalProperties.NETWORK_PORT,
		    GlobalProperties.MAXIMUM_FILE_SIZE, "jetty"));
	    CamelUtil.initProducerTemplate(context.createProducerTemplate());
	    CamelUtil.initConsumerTemplate(context.createConsumerTemplate());
	    context.start();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	logger.info("Camel context initialized");

	// situation handler initialization
	SituationManagerFactory.getSituationManager().init();
    }

    private static void startShutdownListener() {
	new Thread(new Runnable() {

	    @Override
	    public void run() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		try {
		    while (line.equalsIgnoreCase("quit") == false) {
			line = in.readLine();
			if (line.equalsIgnoreCase("routes")) {
			    System.out.println(context.createRouteStaticEndpointJson(null));
			}
		    }
		    logger.info("Shutting Down..");
		    logger.info("Deleting Subscriptions");
		    SituationManagerFactory.getSituationManager().cleanup();
		    logger.info("Stopping Camel");
		    CamelUtil.getProducerTemplate().stop();
		    CamelUtil.getConsumerTemplate().stop();
		    context.stop();
		    logger.info("Shuting down storage access");
		    StorageAccessFactory.closeStorageAccess();
		    logger.info("stopping console listener");
		    in.close();
		    logger.info("Shutting down plugin system.");
		    PluginManagerFactory.shutdownPluginManagement();
		    logger.info("Shutting down notification component.");
		    NotificationComponentFactory.shutdown();
		    logger.info("Shutting down operation handling component.");
		    OperationHandlerFactory.shutdown();
		    logger.info("done");
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}).start();
    }


}

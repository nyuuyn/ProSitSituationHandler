package main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletContextEvent;

import org.apache.camel.CamelContext;
import org.apache.camel.component.servletlistener.ServletCamelContext;
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

/**
 * The Class SituationHandlerInitializer can be used to initialize and close the
 * situation handler. Two different camel components can be used for the startup
 * : jetty for the java-application-mode and servlet for the server-mode.
 */
class SituationHandlerInitializer {

    /** The camel context. */
    private static CamelContext context;

    /** The registry. */
    private static JndiRegistry registry;

    /** The logger. */
    private final static Logger logger = Logger.getLogger(SituationHandlerInitializer.class);

    /**
     * Start as java application.
     */
    static void startAsJavaApplication() {
	context = new DefaultCamelContext();
	registry = context.getRegistry(JndiRegistry.class);

	setRegistryEntries();

	// resource handler for serving the web app
	ResourceHandler webapp = new ResourceHandler();
	try {
	    webapp.setBaseResource(
		    Resource.newResource(new File(SituationHandlerProperties.getWebAppPath())));
	} catch (MalformedURLException e1) {
	    e1.printStackTrace();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
	registry.bind("webApp", webapp);

	SituationHandlerProperties.setHttpEndpointComponent("jetty");
	startRoutes("jetty");
	startSituationHandler();

    }

    /**
     * Start in servlet container.
     *
     * @param sce
     *            servlet context
     */
    static void startInServletContainer(ServletContextEvent sce) {
	registry = new JndiRegistry();
	context = new ServletCamelContext(registry, sce.getServletContext());

	setRegistryEntries();
	SituationHandlerProperties.setHttpEndpointComponent("servlet");
	startRoutes("servlet");
	startSituationHandler();
    }

    /**
     * Sets the registry entries (bean binding).
     */
    private static void setRegistryEntries() {

	// register beans for use
	registry.bind("ruleApi", RuleAPI.class);
	registry.bind("endpointApi", EndpointAPI.class);
	registry.bind("pluginApi", PluginAPI.class);
	registry.bind("historyApi", HistoryAPI.class);

	registry.bind("operationHandlerEndpoint", OperationHandlerEndpoint.class);
	registry.bind("situationEndpoint", SituationEndpoint.class);

    }

    /**
     * Start the camel routes.
     *
     * @param component
     *            the component
     */
    private static void startRoutes(String component) {
	try {
	    // Uncomment this to debug http requests (using fiddler)
	    // context.getProperties().put("http.proxyHost", "localhost");
	    // context.getProperties().put("http.proxyPort", "8888");

	    // add routes
	    context.addRoutes(new SituationHandlerRouteBuilder(component));

	    context.addRoutes(new RestApiRoutes(component));
	    CamelUtil.initCamelContext(context);
	    CamelUtil.initProducerTemplate(context.createProducerTemplate());
	    CamelUtil.initConsumerTemplate(context.createConsumerTemplate());
	    CamelUtil.initExecutorService(context.getExecutorServiceManager().newFixedThreadPool(
		    SituationHandlerInitializer.class, "SitHandlerThread",
		    SituationHandlerProperties.getDefaultThreadPoolSize()));
	    context.start();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	logger.info("Camel context initialized");
    }

    /**
     * Start situation handler components.
     */
    private static void startSituationHandler() {
	// situation handler initialization
	SituationManagerFactory.getSituationManager().init();

	// load Plugins
	PluginManagerFactory.getPluginManager().getAllPluginIDs();
    }

    /**
     * Shutdown. Closes the situation handler and does some cleanup.
     */
    static void shutdown() {
	logger.info("Shutting Down..");
	logger.info("Deleting Subscriptions");
	SituationManagerFactory.getSituationManager().cleanup();
	logger.info("Stopping Camel");
	try {
	    CamelUtil.getProducerTemplate().stop();
	    CamelUtil.getConsumerTemplate().stop();
	    context.stop();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	logger.info("Shuting down storage access");
	StorageAccessFactory.closeStorageAccess();
	logger.info("Shutting down plugin system.");
	PluginManagerFactory.shutdownPluginManagement();
	logger.info("Shutting down notification component.");
	NotificationComponentFactory.shutdown();
	logger.info("Shutting down operation handling component.");
	OperationHandlerFactory.shutdown();
	logger.info("done");
    }

}

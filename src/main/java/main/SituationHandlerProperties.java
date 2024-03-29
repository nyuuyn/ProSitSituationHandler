package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * This class is used to load the situation handler properties from the file
 * system. It allows other classes to access those properties using getters.
 * <p>
 * When a property could not be loaded or is not set in the properites file, a
 * default value is returned.
 * 
 * @author Stefan
 *
 */
public class SituationHandlerProperties {

    /** The logger. */
    private static final Logger logger = Logger.getLogger(SituationHandlerProperties.class);

    /**
     * The name of the properties file.
     */
    private static final String PROPERTIES_FILENAME = "situationHandler.config.properties";

    /**
     * The camel component that is used to provide http components. Either
     * "jetty", "servlet" or "not_set".
     */
    private static String httpEndpointComponent = "not_set";

    /**
     * The properties as loaded from the file system.
     */
    private static Properties properties = new Properties();

    static {
	logger.debug("Loading properties...");
	/*
	 * Load the properties.
	 */
	InputStream inputStream = null;
	try {
	    inputStream = SituationHandlerProperties.class.getClassLoader()
		    .getResourceAsStream(PROPERTIES_FILENAME);
	    if (inputStream == null) {
		throw new FileNotFoundException();
	    }
	    properties.load(inputStream);
	    logger.info("Properties loaded.");
	} catch (IOException e) {
	    logger.error("Could not load properties file.", e);
	} finally {
	    try {
		inputStream.close();
	    } catch (IOException e) {
		logger.error("Error closing stream.", e);
	    }
	}
    }

    /**
     * Gets the network port to use.
     * <p>
     * Only relevant when the situation handler is used without an application
     * server or something (e.g. when using the jetty component).
     * 
     * @return the network port as specified in the properties or 8081 as the
     *         default value.
     */
    public static int getNetworkPort() {
	try {
	    return Integer.parseInt(properties.getProperty("situationHandler.network.port"));
	} catch (NumberFormatException e) {
	    return 8081;
	}
    }

    /**
     * Gets the maximum file size (in bytes) that is allowed for file upload at
     * the rest api. This especially limits the size of plugins that are added
     * to the situation handler using the rest api.
     * <p>
     * Only relevant when the situation handler is used without an application
     * server or something (e.g. when using the jetty component).
     * 
     * @return the value for the file size as specified in the properties or
     *         15_000_000 as the default value.
     */
    public static int getMaximumFilesize() {
	try {
	    return Integer.parseInt(
		    properties.getProperty("situationHandler.network.MaximumFileSize")) * 1_000_000;
	} catch (NumberFormatException e) {
	    return 15_000_000;
	}
    }

    /**
     * Gets the relative path of the http endpoint that is used to receive
     * situation changes.
     * 
     * @return the path to the http endpoint as specified in the properties or
     *         SituationEndpoint as the default value
     */
    public static String getSituationEndpointPath() {
	return properties.getProperty("situationHandler.endpoints.SituationEndpointPath",
		"SituationEndpoint");
    }

    /**
     * Gets the relative path of the http endpoint that is used to receive
     * answers of workflows.
     * 
     * @return the path to the http endpoint as specified in the properties or
     *         AnswerEndpoint as the default value
     */
    public static String getAnswerEndpointPath() {
	return properties.getProperty("situationHandler.endpoints.AnswerEndpointPath",
		"AnswerEndpoint");
    }

    /**
     * Gets the relative path of the http endpoint that is used to receive
     * requests from workflows.
     * 
     * @return the path to the http endpoint as specified in the properties or
     *         RequestEndpoint as the default value
     */
    public static String getRequestEndpointPath() {
	return properties.getProperty("situationHandler.endpoints.RequestEndpointPath",
		"RequestEndpoint");
    }

    /**
     * Gets the relative path of the http endpoint that is used to receive
     * workflow decisions.
     * 
     * @return the path to the http endpoint as specified in the properties or
     *         decisions as the default value
     */
    public static String getDecisionsEndpointPath() {
	return properties.getProperty("situationHandler.endpoints.DecisionEndpointPath",
		"decisions");
    }

    /**
     * Gets the relative path of the http endpoint that is used to receive
     * callbacks by the deployment web service..
     * 
     * @return The path as stated in the properties or "deploymentCallback" as
     *         default.
     */
    public static String getDeploymentCallbackPath() {
	return properties.getProperty("situationHandler.endpoints.DeploymentCallbacktPath",
		"deploymentCallback");
    }

    /**
     * Gets the (relative) base path for the rest api. Api is available under
     * <basepath>/<api basepath>
     * 
     * @return the api base path as specified in the properties or config as the
     *         default value.
     */
    public static String getRestApiBasePath() {
	return properties.getProperty("situationHandler.rest.api.basepath", "config");
    }

    /**
     * Gets the (relative) base path. Used as parent for all other paths.
     * 
     * @return the base path as specified in the properties or situationhandler
     *         as the default value.
     */
    public static String getRestBasePath() {
	return properties.getProperty("situationHandler.rest.basepath", "situationhandler");
    }

    /**
     * Gets the The (relative) base path for the definitions. Definitions are
     * then available under &lt;basepath&gt;/&lt;definitions&gt;
     * 
     * @return the path as specified in the properties or definitions as the
     *         default value.
     */
    public static String getDefinitionsPath() {
	return properties.getProperty("situationHandler.rest.api.definitions", "definitions");
    }

    /**
     * Gets the path to the root folder that contains the web app.
     * <p>
     * Only relevant when the situation handler is used without an application
     * server or something (e.g. when using the jetty component). When using an
     * application server, the web app should better be provided by this server.
     * 
     * @return the path of the root folder as specified in the properties or
     *         C:\\app as the default value.
     */
    public static String getWebAppPath() {
	return properties.getProperty("situationHandler.external.webapp", "C:\\app");
    }

    /**
     * Gets the address of the situation recognition system / SitOpt.
     * 
     * @return the value for srs address as specified in the properties or
     *         http://192.168.209.246:10010 as the default value.
     */
    public static String getSrsAddress() {
	return properties.getProperty("situationHandler.external.srs.Address",
		"http://192.168.209.246:10010");
    }

    /**
     * Gets the path to the plugin folder. Jar-Files in this folder are loaded
     * at startup.
     * 
     * @return the path to the plugin folder as specified in the properties or
     *         plugins as the default value.
     */
    public static String getPluginStartupFolder() {
	return properties.getProperty("situationHandler.plugins.startupFolder", "plugins");
    }

    /**
     * Gets the path to the runtime folder. This folder is used to store jars
     * that are added at runtime.
     * 
     * @return the path to the runtime folder as specified in the properties or
     *         runtime as the default value.
     */
    public static String getPluginRuntimeFolder() {
	return properties.getProperty("situationHandler.plugins.runtimeFolder", "runtime");
    }

    /**
     * Gets the default number of max retries for workflow operations. Used when
     * the workflow request does not specify a number.
     * 
     * @return the number as specified in the properties or 2 as the default
     *         value.
     */
    public static int getDefaultMaxRetries() {
	try {
	    return Integer.parseInt(
		    properties.getProperty("situationHandler.handling.defaultMaxRetries"));
	} catch (NumberFormatException e) {
	    return 2;
	}
    }

    /***
     * 
     * Gets the default size to use for thread pools. Depends on the number of
     * available CPUs.
     * 
     * @return the default size of the thread pool.
     */
    public static int getDefaultThreadPoolSize() {
	return Runtime.getRuntime().availableProcessors() * 2;
    }

    /**
     * Gets the camel component that is used to provide http components.
     * 
     * @return the httpEndpointComponent. Is either "jetty", "servlet" or
     *         "not_set"
     */
    public static String getHttpEndpointComponent() {
	return httpEndpointComponent;
    }

    /**
     * Sets the camel component that is used to provide http components.
     * 
     * @param httpEndpointComponent
     *            the httpEndpointComponent to set. Either "jetty", "servlet" or
     *            "not_set".
     */
    static void setHttpEndpointComponent(String httpEndpointComponent) {
	SituationHandlerProperties.httpEndpointComponent = httpEndpointComponent;
    }

    /**
     * Gets the address of the Bpeldeploymentservice, i.e. the service that is
     * responsible for packing and deploying bpel archives.
     * 
     * @return The address as stated in the properties or
     *         "http://localhost:8083/bpeldeploymentservice" as default.
     */
    public static String getDeploymentServiceAddress() {
	return properties.getProperty("situationHandler.external.deploymentService.address",
		"http://localhost:8083/bpeldeploymentservice");
    }

}

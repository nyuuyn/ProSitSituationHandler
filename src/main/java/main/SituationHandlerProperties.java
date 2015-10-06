package main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SituationHandlerProperties {

    /** The logger. */
    private static final Logger logger = Logger.getLogger(SituationHandlerProperties.class);

    private static Properties properties = new Properties();
    private static final String PROPERTIES_FILENAME = "situationHandler.config.properties";

    static {
	InputStream inputStream = null;
	try {
	    inputStream = SituationHandlerProperties.class.getResourceAsStream(PROPERTIES_FILENAME);

	    properties.load(inputStream);
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

    public static int getNetworkPort() {
	try {
	    return Integer.parseInt(properties.getProperty("situationHandler.network.port"));
	} catch (NumberFormatException e) {
	    return 8081;
	}
    }

    /**
     * Get the maximum file size (in bytes) that is allowed for file upload at
     * the rest api. This especially limits the size of plugins that are added
     * to the situation handler using the rest api.
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

    public static String getSituationEndpointPath() {
	return properties.getProperty("situationHandler.endpoints.SituationEndpointPath",
		"SituationEndpoint");
    }

    public static String getAnswerEndpointPath() {
	return properties.getProperty("situationHandler.endpoints.AnswerEndpointPath",
		"AnswerEndpoint");
    }

    public static String getRequestEndpointPath() {
	return properties.getProperty("situationHandler.endpoints.RequestEndpointPath",
		"RequestEndpoint");
    }

    public static String getRestApiBasePath() {
	return properties.getProperty("situationHandler.rest.basepath", "config");
    }

    /**
     * Get the path to the root folder that contains the web app.
     * <p>
     * Only relevant when using the jetty component.
     * 
     * @return the path of the root folder as specified in the properties or
     *         C:\\app as the default value.
     */
    public static String getWebAppPath() {
	return properties.getProperty("situationHandler.external.webapp", "C:\\app");
    }

    /**
     * Get the address of the situation recognition system / SitOpt.
     * 
     * @return the value for srs address as specified in the properties or
     *         http://192.168.209.246:10010 as the default value.
     */
    public static String getSrsAddress() {
	return properties.getProperty("situationHandler.external.srs.Address",
		"http://192.168.209.246:10010");
    }

    /**
     * Get the path to the plugin folder. Jar-Files in this folder are loaded at
     * startup.
     * 
     * @return the path to the plugin folder as specified in the properties or
     *         plugins as the default value.
     */
    public static String getPluginStartupFolder() {
	return properties.getProperty("situationHandler.plugins.startupFolder", "plugins");
    }

    /**
     * Get the path to the runtime folder. This folder is used to store jars
     * that are added at runtime.
     * 
     * @return the path to the runtime folder as specified in the properties or
     *         runtime as the default value.
     */
    public static String getPluginRuntimeFolder() {
	return properties.getProperty("situationHandler.plugins.runtimeFolder", "runtime");
    }

    public static int getDefaultMaxRetries() {
	try {
	    return Integer.parseInt(
		    properties.getProperty("situationHandler.handling.defaultMaxRetries"));
	} catch (NumberFormatException e) {
	    return 2;
	}
    }

    public static int getDefaultThreadPoolSize() {
	return Runtime.getRuntime().availableProcessors();
    }
}

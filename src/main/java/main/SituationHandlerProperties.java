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

    // #situation handling
    // situationHandler.handling.defaultMaxRetries=2

    public static int getNetworkPort() {
	try {
	    return Integer.parseInt(properties.getProperty("situationHandler.network.port"));
	} catch (NumberFormatException e) {
	    return 8081;
	}
    }

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

    public static String getWebAppPath() {
	return properties.getProperty("situationHandler.external.webapp",
		"C:\\Users\\Stefan\\workspace_Masterarbeit\\SituationHandler_WebApp\\app");
    }

    public static String getSrsAddress() {
	return properties.getProperty("situationHandler.external.srs.Address",
		"http://192.168.209.246:10010");
    }

    public static String getPluginStartupFolder() {
	return properties.getProperty("situationHandler.plugins.startupFolder", "Plugins");
    }

    public static String getPluginRuntimeFolder() {
	return properties.getProperty("situationHandler.plugins.runtimeFolder", "Runtime");
    }

    public static int getDefaultMaxRetries() {
	try {
	    return Integer.parseInt(
		    properties.getProperty("situationHandler.handling.defaultMaxRetries"));
	} catch (NumberFormatException e) {
	    return 2;
	}
    }

    public static final int NETWORK_PORT = 8081;

    public static final String ANSWER_ENDPOINT_PATH = "AnswerEndpoint";
    public static final String SITUATION_ENDPOINT_PATH = "SituationEndpoint";

    /**
     * The maximum file size (in bytes) that is allowed for file upload at
     * the rest api. This especially limits the size of plugins that are added
     * to the situation handler using the rest api.
     */
    public static final int MAXIMUM_FILE_SIZE = 15_000_000;
    public static final int DEFAULT_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * The address of the situation recognition system / SitOpt.
     */
    public static final String SRS_ADDRESS = "http://192.168.209.246:10010";

    /**
     * The root folder that contains the web app.
     */
    public static final String WEB_APP_PATH = "C:\\Users\\Stefan\\workspace_Masterarbeit\\SituationHandler_WebApp\\app";
}

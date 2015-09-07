package main;

public class GlobalProperties {

    public static final int NETWORK_PORT = 8081;

    public static final String ANSWER_ENDPOINT_PATH = "AnswerEndpoint";
    public static final String SITUATION_ENDPOINT_PATH = "SituationEndpoint";

    /**
     * The maximum file size (in kilobytes) that is allowed for file upload at
     * the rest api. This especially limits the size of plugins that are added
     * to the situation handler using the rest api.
     */
    public static final int MAXIMUM_FILE_SIZE = 15_000_000;
    public static final int DEFAULT_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * The address of the situation recognition system / SitOpt.
     */
    public static final String SRS_ADDRESS = "http://192.168.209.200:10010";

    /**
     * The root folder that contains the web app.
     */
    public static final String WEB_APP_PATH = "C:\\Users\\Stefan\\workspace_Masterarbeit\\SituationHandler_WebApp\\app";
}

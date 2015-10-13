package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * The Class Main is used to handle the startup and shutdown of the sitaution
 * handler when it is started as a java application.
 */
public class Main {

    /** The logger. */
    private final static Logger logger = Logger.getLogger(Main.class);

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
	logger.info("Starting as java application.");
	startShutdownListener();
	SituationHandlerInitializer.startAsJavaApplication();
    }

    /**
     * Start the shutdown listener. Listens to console input and inits the
     * shutdown if required.
     */
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
			    logger.info(CamelUtil.getCamelContext()
				    .createRouteStaticEndpointJson(null));
			}
		    }
		    SituationHandlerInitializer.shutdown();
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}).start();
    }

}

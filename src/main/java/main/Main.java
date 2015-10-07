package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
	logger.info("Starting as java application.");
	startShutdownListener();
	SituationHandlerInitializer.startAsJavaApplication();
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
			    System.out.println(CamelUtil.getCamelContext()
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

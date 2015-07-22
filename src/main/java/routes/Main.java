package routes;

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

import situationHandling.storage.StorageAccessFactory;
import api.configuration.EndpointAPI;
import api.configuration.PluginAPI;
import api.configuration.RuleAPI;

public class Main {

	private static CamelContext context;

	private final static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		startShutdownListener();
		shutdownHandling();

		context = new DefaultCamelContext();

		JndiRegistry registry = context.getRegistry(JndiRegistry.class);
				
		
		// register beans for use
		registry.bind("ruleApi", RuleAPI.class);
		registry.bind("endpointApi", EndpointAPI.class);
		registry.bind("pluginApi", PluginAPI.class);
		
		//resource handler for serving the web app
		ResourceHandler webapp = new ResourceHandler();

		
		try {
			webapp.setBaseResource(Resource.newResource(new File("C:\\Users\\Stefan\\workspace_Masterarbeit\\SituationHandler_WebApp\\app")));
			} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		registry.bind("webApp", webapp);




		try {
			// add routes
			context.addRoutes(new SituationHandlerRouteBuilder("0.0.0.0", 8081));
			context.addRoutes(new RestApiRoutes("0.0.0.0", 8081, 15000000, "jetty"));
			context.addRoutes(new SRSRoutes("0.0.0.0", 8081));
			CamelUtil.initProducerTemplate(context.createProducerTemplate());
			CamelUtil.initConsumerTemplate(context.createConsumerTemplate());

			context.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("Camel context initialized");
	}

	// Maybe for later use :)
	private static void shutdownHandling() {
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			public void run() {
//
//			}
//		});
	}

	private static void startShutdownListener() {
		// TODO: Das hier schöner machen
		// for shutdown
		new Thread(new Runnable() {

			@Override
			public void run() {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						System.in));
				String line = "";
				try {
					while (line.equalsIgnoreCase("quit") == false) {
						line = in.readLine();
						if (line.equalsIgnoreCase("routes")){
							System.out.println(context.createRouteStaticEndpointJson(null));
						}else if (line.equalsIgnoreCase("endpoint")){
//							System.out.println(context.explainEipJson("blubb", true));
						}
					}
					logger.info("Shutting Down..");
					CamelUtil.getProducerTemplate().stop();
					CamelUtil.getConsumerTemplate().stop();
					logger.info("Stopping Camel");
					context.stop();
					logger.info("Shuting down storage access");
					StorageAccessFactory.closeStorageAccess();
					logger.info("stopping console listener");
					in.close();
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

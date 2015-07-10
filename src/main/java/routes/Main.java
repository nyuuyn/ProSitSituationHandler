package routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.camel.CamelContext;
import org.apache.camel.component.netty4.http.NettyHttpComponent;
import org.apache.camel.component.netty4.http.NettyHttpConfiguration;
import org.apache.camel.component.netty4.http.NettyHttpEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.log4j.Logger;

import situationHandling.storage.HibernateUtil;
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

		try {
			// add routes
			context.addRoutes(new SituationHandlerRouteBuilder("0.0.0.0", 8081));
			context.addRoutes(new RestApiRoutes("0.0.0.0", 8081, 15000000));
			CamelUtil.initProducerTemplate(context.createProducerTemplate());

			context.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("Camel context initialized");
	}

	// Maybe for later use :)
	private static void shutdownHandling() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {

			}
		});
	}

	private static void startShutdownListener() {
		// TODO: Das hier sch�ner machen
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
					context.stop();
					HibernateUtil.getSessionFactory().close();

					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}

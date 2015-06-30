package routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;

import situationHandling.storage.HibernateUtil;

public class Main {

	private static CamelContext context;
	

	public static void main(String[] args) {
		startShutdownListener();
		shutdownHandling();

		context = new DefaultCamelContext();

		try {
			context.addRoutes(new SituationHandlerRouteBuilder());
			CamelUtil.initProducerTemplate(context.createProducerTemplate());
			context.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//Maybe for later use :)
	private static void shutdownHandling() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				
			}
		});
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
					}
					System.out.println("Shutting Down..");
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

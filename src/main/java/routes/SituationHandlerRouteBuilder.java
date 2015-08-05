package routes;

import org.apache.camel.builder.RouteBuilder;

import situationHandling.SoapProcessor;

class SituationHandlerRouteBuilder extends RouteBuilder {
	private String hostname;
	private int port;

	public SituationHandlerRouteBuilder(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public void configure() {

		// TODO: Hint für Komponente: Aktuell geht mit Jetty das CORS Zeug nicht
		// richtig. Das ist allerdings egal, solange ich die Web app ebenfall
		// mit Jetty anbiete, da ich dann alles auf dem gleichen Port laufen
		// lassen kann. Wenn ich die WebApp aus irgend einem Grund aber woanders
		// deployen will, muss ich da nen anderen Port nehmen --> CORS nötig -->
		// Es muss wieder auf netty4-http gewechselt werden (außer da, wo die
		// webapp geserved wird)

		// TODO: Jetty Componente so gut?

		// TODO: bei Workflow Requests auf Validität prüfen?

		createRequestEndpoint();
		createRequestAnswerEndpoint();
		createSubscriptionEndpoint();
		setCorsHeaders();
		serveWebapp();

	}

	private void createRequestEndpoint() {
		// forward each message posted on .../RequestEndpoint to the operation
		// Handler. Requests are answered immediately and sent
		// to a queue for asynchronous processing. Several threads are used to
		// consume from the queue.

		from(
				"jetty:http://" + hostname + ":" + port
						+ "/RequestEndpoint?matchOnUriPrefix=true")
				// .to("stream:out")
				.process(new SoapProcessor())
				.to("seda:workflowRequests?waitForTaskToComplete=Never")
				.transform(constant("Ok"));

		from(
				"seda:workflowRequests?concurrentConsumers="
						+ GlobalProperties.DEFAULT_THREAD_POOL_SIZE).to(
				"bean:operationHandlerEndpoint?method=receiveRequest");
	}

	private void createRequestAnswerEndpoint() {
		// forward each message posted on .../AnswerEndpoint to the appropriate
		// Handler. Requests are answered immediately and sent
		// to a queue for asynchronous processing. Several threads are used to
		// consume from the queue.
		from(
				"jetty:http://" + hostname + ":" + port + "/"
						+ GlobalProperties.ANSWER_ENDPOINT_PATH
						+ "?matchOnUriPrefix=true")
				// .to("stream:out")
				.process(new SoapProcessor())
				.to("seda:answeredRequests?waitForTaskToComplete=Never")
				.transform(constant("Ok"));

		from(
				"seda:answeredRequests?concurrentConsumers="
						+ GlobalProperties.DEFAULT_THREAD_POOL_SIZE).to(
				"bean:operationHandlerEndpoint?method=receiveAnswer");
	}

	private void createSubscriptionEndpoint() {
		// to receive Subscriptions. Requests are answered immediately and sent
		// to a queue for asynchronous processing. Several threads are used to
		// consume from the queue.
		from(
				"jetty:http://" + hostname + ":" + port + "/"
						+ GlobalProperties.SITUATION_ENDPOINT_PATH).to(
				"seda:situationChange?waitForTaskToComplete=Never").transform(
				constant("Ok"));
		from(
				"seda:situationChange?concurrentConsumers="
						+ GlobalProperties.DEFAULT_THREAD_POOL_SIZE).to(
				"bean:situationEndpoint?method=situationReceived");
	}

	private void setCorsHeaders() {
		// set CORS Headers for option requests and set max file size
		from(
				"jetty:http://" + hostname + ":" + port
						+ "/api-docs?httpMethodRestrict=OPTIONS")
				.setHeader("Access-Control-Allow-Origin")
				.constant("*")
				.setHeader("Access-Control-Allow-Methods")
				.constant(
						"GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH")
				.setHeader("Access-Control-Allow-Headers")
				.constant(
						"Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, x-file-last-modified,x-file-name,x-file-size");

	}

	private void serveWebapp() {
		// used for serving the wep app
		from("jetty:http://0.0.0.0:8081?handlers=#webApp").to("stream:out");
	}
}

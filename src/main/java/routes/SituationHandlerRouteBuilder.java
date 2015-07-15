package routes;

import org.apache.camel.builder.RouteBuilder;

import situationHandling.OperationHandlerImpl;

class SituationHandlerRouteBuilder extends RouteBuilder {
	private String hostname;
	private int port;

	public SituationHandlerRouteBuilder(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public void configure() {

		// TODO: den gleichen server für alles benutzen
		// forward each message posted on .../SoapEndpoint to the operation
		// Handler
		from(
				"netty4-http:http://" + hostname + ":" + port
						+ "/SoapEndpoint?matchOnUriPrefix=true").to(
				"stream:out").bean(OperationHandlerImpl.class);

		from("jetty:http://0.0.0.0:8082?handlers=#webApp").to("stream:out");

	}

}

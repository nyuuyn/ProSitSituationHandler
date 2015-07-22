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

		// TODO: Hint für Komponente: Aktuell geht mit Jetty das CORS Zeug nicht
		// richtig. Das ist allerdings egal, solange ich die Web app ebenfall
		// mit Jetty anbiete, da ich dann alles auf dem gleichen Port laufen
		// lassen kann. Wenn ich die WebApp aus irgend einem Grund aber woanders
		// deployen will, muss ich da nen anderen Port nehmen --> CORS nötig -->
		// Es muss wieder auf netty4-http gewechselt werden (außer da, wo die
		// webapp geserved wird)

		// TODO: den gleichen server für alles benutzen
		// forward each message posted on .../SoapEndpoint to the operation
		// Handler
		from(
				"jetty:http://" + hostname + ":" + port
						+ "/SoapEndpoint?matchOnUriPrefix=true").to(
				"stream:out").bean(OperationHandlerImpl.class);

		// set CORS Headers for option requests and max file size
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

		// used for serving the wep app
		from("jetty:http://0.0.0.0:8081?handlers=#webApp").to("stream:out");
	}

}

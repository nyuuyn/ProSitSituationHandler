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

		// TODO: Hier den Endpunkt konfigurieren und den dann überall benutzen?
		// Wie geht es, da z.B. verschiedene Einstellungen zu benutzen?
		// Endpoint ep = endpoint("jetty:http://0.0.0.0:8080");

		// JettyHttpComponent jetty = new JettyHttpComponent8();

		// TODO: den gleichen server für alles benutzen

		// by using 0.0.0.0, the jetty server is exposed on all network
		// interfaces
		from(
				"jetty:http://" + hostname + ":" + port
						+ "/SoapEndpoint?matchOnUriPrefix=true").to(
				"stream:out").bean(OperationHandlerImpl.class);

	}

}

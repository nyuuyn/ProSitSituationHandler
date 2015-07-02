package routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import situationHandling.OperationHandlerImpl;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

class SituationHandlerRouteBuilder extends RouteBuilder {

	public void configure() {

		// TODO: Hier den Endpunkt konfigurieren und den dann überall benutzen?
		// Wie geht es, da z.B. verschiedene Einstellungen zu benutzen?
		// Endpoint ep = endpoint("jetty:http://0.0.0.0:8080");

		// JettyHttpComponent jetty = new JettyHttpComponent8();

		// TODO: den gleichen server für alles benutzen

		// by using 0.0.0.0, the jetty server is exposed on all network
		// interfaces
		from("jetty:http://0.0.0.0:8080/SoapEndpoint?matchOnUriPrefix=true")
				.to("stream:out").bean(OperationHandlerImpl.class);


	}

}

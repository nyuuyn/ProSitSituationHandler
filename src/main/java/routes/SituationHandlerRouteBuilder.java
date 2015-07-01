package routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import situationHandling.OperationHandlerImpl;
import situationHandling.storage.datatypes.Rule;
import api.configuration.RuleAPI;

public class SituationHandlerRouteBuilder extends RouteBuilder {

	public void configure() {

		// by using 0.0.0.0, the jetty server is exposed on all network
		// interfaces
		from("jetty:http://0.0.0.0:8080/SoapEndpoint?matchOnUriPrefix=true")
				.to("stream:out").bean(OperationHandlerImpl.class);

		rest("/config").description("User rest service")
				.consumes("application/json").produces("application/json")

				.get("/rules").outTypeList(Rule.class).to("direct:getRules").get("/rules/{id}")
				.to("direct:getRuleByID");

		// Könnte man auch so machen, erfordert aber Bean registrierung
		// .get("/rules").to("bean:RuleAPI?method=getRules");

		// TODO: Was ist mit den Consumes/Produces dinger?

		from("direct:getRules").bean(RuleAPI.class, "getRules");
		// from("direct:getRules").bean(RuleAPI.class);

		from("direct:getRuleByID").bean(RuleAPI.class, "getRuleByID");
		// ID der Regel könnte auch als Parameter übergeben werden!

		restConfiguration().component("jetty").port(8081).host("0.0.0.0")
				.bindingMode(RestBindingMode.json)
				.dataFormatProperty("prettyPrint", "true");

		//
		// rest("/say").get("/hello").to("direct:hello").get("/bye")
		// .consumes("application/json").to("direct:bye").post("/bye")
		// .to("mock:update");
		//
		// from("direct:hello").transform().constant("<p>Hello World</p>");
		// from("direct:bye").transform().constant("Bye World");
		//

	}

}

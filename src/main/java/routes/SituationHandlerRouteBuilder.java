package routes;

import org.apache.camel.builder.RouteBuilder;

import situationHandling.SituationHandlerImpl;

public class SituationHandlerRouteBuilder extends RouteBuilder {

	public void configure() {

		// by using 0.0.0.0, the jetty server is exposed on all network
		// interfaces
		from("jetty:http://0.0.0.0:8080/SoapEndpoint?matchOnUriPrefix=true").to("stream:out")
				.bean(SituationHandlerImpl.class);
		//
		// rest("/say").get("/hello").to("direct:hello").get("/bye")
		// .consumes("application/json").to("direct:bye").post("/bye")
		// .to("mock:update");
		//
		// from("direct:hello").transform().constant("<p>Hello World</p>");
		// from("direct:bye").transform().constant("Bye World");
		//
		// restConfiguration().component("jetty")
		// .port(8080).host("localhost");

	}

}

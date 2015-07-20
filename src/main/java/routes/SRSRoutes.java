package routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

class SRSRoutes extends RouteBuilder {

	private String hostname;
	private int port;

	SRSRoutes(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	@Override
	public void configure() throws Exception {

		from("netty4-http:http://" + hostname + ":" + port + "/SituationEndpoint").to("stream:out")
				.process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						exchange.getOut().setBody("");

					}
				});
	}

}

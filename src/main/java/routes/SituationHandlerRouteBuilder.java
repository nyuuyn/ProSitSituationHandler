package routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
		
		// set CORS Headers for option requests and max file size
		from(
				"netty4-http:http://"
						+ hostname
						+ ":"
						+ port
						+ "/api-docs?httpMethodRestrict=OPTIONS")
				.setHeader("Access-Control-Allow-Origin")
				.constant("*")
				.setHeader("Access-Control-Allow-Methods")
				.constant(
						"GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH")
				.setHeader("Access-Control-Allow-Headers")
				.constant(
						"Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, x-file-last-modified,x-file-name,x-file-size");

		from("jetty:http://0.0.0.0:8082?handlers=#webApp").to("stream:out");

	}

}

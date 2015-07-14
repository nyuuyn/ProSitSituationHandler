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


		from("netty4-http:http://" + hostname + ":" + port + "/api-docs")
				.process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						exchange.getIn()
								.setBody(
										"{\r\n  \"swaggerVersion\": \"1.2\",\r\n  \"basePath\": \"http://localhost:8000/greetings\",\r\n  \"apis\": [\r\n    {\r\n      \"path\": \"/hello/{subject}\",\r\n      \"operations\": [\r\n        {\r\n          \"method\": \"GET\",\r\n          \"summary\": \"Greet our subject with hello!\",\r\n          \"type\": \"string\",\r\n          \"nickname\": \"helloSubject\",\r\n          \"parameters\": [\r\n            {\r\n              \"name\": \"subject\",\r\n              \"description\": \"The subject to be greeted.\",\r\n              \"required\": true,\r\n              \"type\": \"string\",\r\n              \"paramType\": \"path\"\r\n            }\r\n          ]\r\n        }\r\n      ]\r\n    }\r\n  ],\r\n  \"models\": {}\r\n}",
										String.class);

					}
				});

	}

}

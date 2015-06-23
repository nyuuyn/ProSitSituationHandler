package routes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.camel.builder.RouteBuilder;

import situationHandling.SituationHandlerImpl;

public class SituationHandlerRouteBuilder extends RouteBuilder {

	public void configure() {

		// by using 0.0.0.0, the jetty server is exposed on all network
		// interfaces
		from("jetty:http://0.0.0.0:8080/SoapEndpoint?matchOnUriPrefix=true").bean(
				SituationHandlerImpl.class);
//
//		rest("/say").get("/hello").to("direct:hello").get("/bye")
//				.consumes("application/json").to("direct:bye").post("/bye")
//				.to("mock:update");
//
//		from("direct:hello").transform().constant("<p>Hello World</p>");
//		from("direct:bye").transform().constant("Bye World");
//		
//        restConfiguration().component("jetty")
//        .port(8080).host("localhost");


		// from("jetty:http://localhost:8181?matchOnUriPrefix=true").process(
		// new Processor() {
		// public void process(Exchange exchange) throws Exception {
		//
		// String message = exchange.getIn().getBody(String.class);
		//
		// String op = getOperationName(message);
		//
		// String situation = new SituationManager()
		// .getSituation();
		// String endpointAddress = new EndpointDirectory()
		// .getEndpoint(op, situation);
		//
		// exchange.getIn().setBody(
		// httpPost(endpointAddress, message));
		// System.out.println(exchange.hasOut());
		// }
		// }).to("file://test");

		// from("jetty:http://localhost:8181?matchOnUriPrefix=true").process(
		//
		// new Processor() {
		// public void process(Exchange exchange) throws Exception {
		//
		// String message = exchange.getIn().getBody(String.class);
		// exchange.getOut().setBody(message);
		// }
		//
		// }).dynamicRouter(method(DynamicRouterTest.class, "slip"));
		//

	}

	private String getOperationName(String soapString) {
		System.out.println(soapString);

		InputStream inputStream = new ByteArrayInputStream(soapString
				.toString().getBytes());
		String operation = "";
		try {
			SOAPMessage soapReq = MessageFactory.newInstance().createMessage(
					null, inputStream);

			operation = soapReq.getSOAPPart().getEnvelope().getBody()
					.getChildNodes().item(1).getNodeName();

		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Parsed Operation name: ");
		System.out.print(operation);
		return operation;

	}

	private String httpPost(String endpointAddress, String soapString) {

		System.out.println("Forwarding Request");
		java.net.URL url;
		try {
			url = new java.net.URL(endpointAddress);

			java.net.URLConnection conn = url.openConnection();
			// Set the necessary header fields
			conn.setRequestProperty("SOAPAction", "");
			conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			conn.setDoOutput(true);
			// Send the request
			java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(
					conn.getOutputStream());
			wr.write(soapString.toString());
			wr.flush();
			// Read the response
			java.io.BufferedReader rd = new java.io.BufferedReader(
					new java.io.InputStreamReader(conn.getInputStream()));

			System.out.println("Answer:");
			String line = "";
			StringBuilder answer = new StringBuilder();
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
				answer.append(line);
			}

			return answer.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}

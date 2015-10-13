package main;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import utils.soap.SoapProcessor;

/**
 * The Class SituationHandlerRouteBuilder creates the http-endpoints used for
 * the handling of workflows and so on.
 */
class SituationHandlerRouteBuilder extends RouteBuilder {

    /**
     * The base path that defines the component and the address (if necessary).
     */
    private String path;

    /**
     * The camel component to provide the http endpoints.
     */
    private String component;

    /**
     * Instantiates a new situation handler route builder.
     *
     * @param component
     *            the component that provides the http-endpoints. Use either
     *            "jetty" or "servlet".
     */
    public SituationHandlerRouteBuilder(String component) {

	if (component.equals("jetty")) {
	    path = "jetty:http://0.0.0.0:" + SituationHandlerProperties.getNetworkPort() + "/"
		    + SituationHandlerProperties.getRestBasePath();
	} else if (component.equals("servlet")) {
	    path = "servlet://";
	} else {
	    throw new IllegalArgumentException("Unsupported Component: " + component);
	}
	this.component = component;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.builder.RouteBuilder#configure()
     */
    public void configure() {
	createRequestEndpoint();
	createRequestAnswerEndpoint();
	createSubscriptionEndpoint();
	setCorsHeaders();
	if (component.equals("jetty")) {
	    serveWebapp();
	}
    }

    /**
     * Creates the endpoint to receive workflow requests.
     */
    private void createRequestEndpoint() {
	// forward each message posted on .../RequestEndpoint to the operation
	// Handler. Requests are answered immediately and sent
	// to a queue for asynchronous processing. Several threads are used to
	// consume from the queue.
	from(path + "/" + SituationHandlerProperties.getRequestEndpointPath()
		+ "?matchOnUriPrefix=true").process(new SoapProcessor())
			.to("seda:workflowRequests?waitForTaskToComplete=Never")
			.transform(constant(""))
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant("202"));

	from("seda:workflowRequests?concurrentConsumers="
		+ SituationHandlerProperties.getDefaultThreadPoolSize())
			.to("bean:operationHandlerEndpoint?method=receiveRequest");
    }

    /**
     * Creates the endpoint to receive workflow answers.
     */
    private void createRequestAnswerEndpoint() {
	// forward each message posted on .../AnswerEndpoint to the appropriate
	// Handler. Requests are answered immediately and sent
	// to a queue for asynchronous processing. Several threads are used to
	// consume from the queue.
	from(path + "/" + SituationHandlerProperties.getAnswerEndpointPath()
		+ "?matchOnUriPrefix=true")
			// .to("stream:out")
			.process(new SoapProcessor())
			.to("seda:answeredRequests?waitForTaskToComplete=Never")
			.transform(constant(""))
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant("202"));
	;

	from("seda:answeredRequests?concurrentConsumers="
		+ SituationHandlerProperties.getDefaultThreadPoolSize())
			.to("bean:operationHandlerEndpoint?method=receiveAnswer");
    }

    /**
     * Creates the endpoint to receive situation changes.
     */
    private void createSubscriptionEndpoint() {
	// to receive Subscriptions. Requests are answered immediately and sent
	// to a queue for asynchronous processing. Several threads are used to
	// consume from the queue.
	from(path + "/" + SituationHandlerProperties.getSituationEndpointPath())
		.to("seda:situationChange?waitForTaskToComplete=Never").transform(constant("Ok"));
	// no conucurrent consumers here!
	from("seda:situationChange").to("bean:situationEndpoint?method=situationReceived");
    }

    /**
     * Helper method to set the cors headers.
     */
    private void setCorsHeaders() {
	// set CORS Headers for option requests
	from(path + "/api-docs?httpMethodRestrict=OPTIONS").setHeader("Access-Control-Allow-Origin")
		.constant("*").setHeader("Access-Control-Allow-Methods")
		.constant("GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH")
		.setHeader("Access-Control-Allow-Headers").constant(
			"Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, x-file-last-modified,x-file-name,x-file-size");

    }

    /**
     * Provides the wepapp.
     */
    private void serveWebapp() {
	// used for serving the wep app
	from(path + "?handlers=#webApp").to("stream:out");
    }
}

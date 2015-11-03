package situationHandling.workflowOperations;

import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import main.CamelUtil;
import main.SituationHandlerProperties;
import situationHandling.storage.datatypes.Endpoint;
import utils.soap.WsaSoapMessage;

/**
 * TODO
 * 
 * @author Stefan
 *
 */
class DeploymentHandler implements Runnable {
    /** The logger. */
    private static final Logger logger = Logger.getLogger(DeploymentHandler.class);

    private OperationHandlerImpl callbackHandler;
    /**
     * The request by the workflow
     */
    private WsaSoapMessage wsaSoapMessage;

    /**
     * The rollbackhandler used to handle the workflow request.
     */
    private RollbackHandler rollbackHandler;

    private Endpoint endpointToDeploy;

    /**
     * @param callbackHandler
     * @param wsaSoapMessage
     * @param rollbackHandler
     * @param endpointToDeploy
     */
    DeploymentHandler(OperationHandlerImpl callbackHandler, WsaSoapMessage wsaSoapMessage,
	    RollbackHandler rollbackHandler, Endpoint endpointToDeploy) {
	this.callbackHandler = callbackHandler;
	this.wsaSoapMessage = wsaSoapMessage;
	this.rollbackHandler = rollbackHandler;
	this.endpointToDeploy = endpointToDeploy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
	String requestId = UUID.randomUUID().toString();

	// create callback http endpoint and handle error
	if (!createCallbackEndpoint(requestId)) {
	    logger.warn("Could not create callback endpoint");
	    callbackHandler.deploymentCallback(false, wsaSoapMessage, rollbackHandler);
	    return;
	}

	// send request and wait for answer if successful
	if (!sendDecisionRequest()) {
	    logger.warn("Could not send message to decider.");
	    callbackHandler.deploymentCallback(false, wsaSoapMessage, rollbackHandler);
	} else {
	    // wait for answer
	    synchronized (this) {
		try {
		    this.wait();
		} catch (InterruptedException e) {
		    logger.error("Interrupted", e);
		}
	    }
	}
	
	//TODO: irgendwo muss ich dann noch den Endpunkt updaten

	closeCallbackEndpoint(requestId);
    }

    /**
     * Creates an http endpoint to receive the answer to a decision request. The
     * url of the endpoint contains the id of the request. The endpoint is
     * created in a camel route.
     * 
     * @param requestId
     *            the id of the decision request
     * @return true if the endpoint was successfully created, false else
     */
    private boolean createCallbackEndpoint(String requestId) {
	String camelComponent = SituationHandlerProperties.getHttpEndpointComponent();
	// set path for camel component
	String path;
	if (camelComponent.equals("jetty")) {
	    path = camelComponent + ":http://0.0.0.0:" + SituationHandlerProperties.getNetworkPort()
		    + "/" + SituationHandlerProperties.getRestBasePath() + "/"
		    + SituationHandlerProperties.getDecisionsEndpointPath() + "/" + requestId;
	} else if (camelComponent.equals("servlet")) {
	    path = camelComponent + ":///" + SituationHandlerProperties.getDecisionsEndpointPath()
		    + "/" + requestId;
	} else {
	    logger.warn("Invalid camel component: " + camelComponent);
	    return false;
	}

	DeploymentHandler thisHandler = this;

	/*
	 * Create processor. The processor is used on the camel route to process
	 * the answer. The processor parses the message and makes a callback to
	 * this thread (i.e. notifies it)
	 */
	Processor resultHandler = new Processor() {
	    @Override
	    public void process(Exchange exchange) throws Exception {
		String answerString = exchange.getIn().getBody(String.class);
		ObjectMapper mapper = new ObjectMapper();
		DeployResponse answer = mapper.readValue(answerString, DeployResponse.class);

		callbackHandler.deploymentCallback(answer.isSuccess(), wsaSoapMessage,
			rollbackHandler);

		// create answer (useless JSON object to follow conventions..)
		exchange.getIn().setBody("{\"result\" : \"success\"}");
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);

		synchronized (thisHandler) {
		    thisHandler.notify();
		}
	    }
	};
	// create camel route
	try {
	    CamelUtil.getCamelContext().addRoutes(new DynamicEndpointBuilder(
		    CamelUtil.getCamelContext(), path, resultHandler, requestId));
	} catch (Exception e) {
	    logger.error("Could not create camel route.", e);
	    return false;
	}
	return true;
    }

    /**
     * Closes the http endpoint that is used to received the answer.
     * 
     * @param routeId
     *            the id of the camel route that provides the endpoint.
     */
    private void closeCallbackEndpoint(String routeId) {
	try {
	    CamelUtil.getCamelContext().stopRoute(routeId);
	    CamelUtil.getCamelContext().removeRoute(routeId);
	} catch (Exception e) {
	    logger.error("Could not stop/remove route.", e);
	}
    }

    /**
     * TODO
     */
    private boolean sendDecisionRequest() {
	// send request
	// TODO
	return false;
    }

}

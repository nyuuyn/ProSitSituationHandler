package situationHandling.workflowOperations;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import main.CamelUtil;
import main.SituationHandlerProperties;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Endpoint.EndpointStatus;
import utils.soap.WsaSoapMessage;

/**
 * The class DeploymentHandler is used to communicate with the deployment web
 * service. It sends a deploy request to the service and waits for its callback.
 * The callback is then forwarded to an instance of {@code OperationHandlerImpl}
 * .
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

    /**
     * The endpoint that is to be deployed.
     */
    private Endpoint endpointToDeploy;

    /**
     * 
     * Creates a new instance of DeploymentHandler.
     * 
     * @param callbackHandler
     *            the operationhandler for callback
     * @param wsaSoapMessage
     *            the message that is handler
     * @param rollbackHandler
     *            the rollbackhandler to use
     * @param endpointToDeploy
     *            the endpoint (archive) to deploy
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

	logger.info("Deploying new endpoint: " + endpointToDeploy.toString());
	// create callback http endpoint and handle error
	if (!createCallbackEndpoint(requestId)) {
	    logger.warn("Could not create callback endpoint");
	    callbackHandler.deploymentCallback(false, wsaSoapMessage, rollbackHandler);
	    return;
	}

	logger.info("Sending deployment request for " + endpointToDeploy.toString());
	// send request and wait for answer if successful
	if (!sendDeploymentRequest(requestId)) {
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
		    + SituationHandlerProperties.getDeploymentCallbackPath() + "/" + requestId;
	} else if (camelComponent.equals("servlet")) {
	    path = camelComponent + ":///" + SituationHandlerProperties.getDeploymentCallbackPath()
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

		// update Endpoint
		if (answer.isSuccess()) {
		    StorageAccessFactory.getEndpointStorageAccess().updateEndpoint(
			    endpointToDeploy.getEndpointID(), null, null, null, null,
			    answer.getEndpointUrl(), null, EndpointStatus.available);
		    logger.info("Successfully deployed process archive "
			    + endpointToDeploy.getArchiveFilename() + ". New Url is: "
			    + answer.getEndpointUrl());
		} else {
		    logger.warn("Deployment failed for process archive "
			    + endpointToDeploy.getArchiveFilename());
		}

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
     * Sends a deployment request to the deployment web service.
     * 
     * @param requestId
     *            the request id (must be the same than used in the endpoint)
     * @return true, when the request was sent successfully, false else.
     */
    private boolean sendDeploymentRequest(String requestId) {
	ProducerTemplate pt = CamelUtil.getProducerTemplate();

	// set headers
	Map<String, Object> headers = new HashMap<>();
	headers.put(Exchange.HTTP_METHOD, "POST");
	headers.put(Exchange.HTTP_PATH, "/deploy"); // path
	headers.put("Content-Type", "application/json");
	headers.put("Accept", "text/plain");

	try {

	    // create path for callback and send request
	    String callbackUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":"
		    + SituationHandlerProperties.getNetworkPort() + "/"
		    + SituationHandlerProperties.getRestBasePath() + "/"
		    + SituationHandlerProperties.getDeploymentCallbackPath() + "/" + requestId;
	    DeployRequest deployRequest = new DeployRequest(endpointToDeploy.getArchiveFilename(),
		    callbackUrl);

	    pt.requestBodyAndHeaders(SituationHandlerProperties.getDeploymentServiceAddress(),
		    new ObjectMapper().writer().writeValueAsString(deployRequest), headers,
		    String.class);
	    logger.debug("Successfully sent deploy request for fragment archive "
		    + endpointToDeploy.getArchiveFilename());
	    return true;
	} catch (CamelExecutionException | UnknownHostException | JsonProcessingException e) {
	    logger.error("Error sending deploy request for fragment "
		    + endpointToDeploy.getArchiveFilename(), e);
	    return false;
	}
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

}

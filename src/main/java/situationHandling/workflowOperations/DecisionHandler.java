package situationHandling.workflowOperations;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import main.CamelUtil;
import main.SituationHandlerProperties;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.PluginParams;
import situationHandling.storage.datatypes.Endpoint;
import utils.soap.WsaSoapMessage;

/**
 * 
 * The class DecisionHandler is used to send Workflow Decisions and to handle
 * the Result.
 * <p>
 * A decision request is sent to a user, when the situation handler could was
 * not able to find a unique endpoint to handle a workflow request and when the
 * workflow request specified a user to make the decision.
 * <p>
 * The DecisionHandler is a thread that sends the decision request and then
 * waits until the result arrives. The receive the result, an http endpoint is
 * created. The url of the endpoint contains the id of the request and is only
 * used to recieve a single decision. The endpoint is closed after the decision
 * was received.
 * <p>
 * When processing is finished, a callback to an Instance of
 * {@code OperationHandlerImpl} is made.
 * 
 * @see OperationHandlerImpl
 * @see DecisionAnswer
 * 
 * @author Stefan
 *
 */
class DecisionHandler implements Runnable {

    /** The logger. */
    private static final Logger logger = Logger.getLogger(MessageRouter.class);

    /**
     * The candidates for the decision. One of the candidates must be chosen.
     */
    private List<Endpoint> candidateEndpoints;

    /**
     * An instance of OperationHandlerImpl that handles the callback of the
     * decision handler.
     */
    private OperationHandlerImpl callbackOperationHandler;

    /**
     * The request by the workflow
     */
    private WsaSoapMessage wsaSoapMessage;

    /**
     * The rollbackhandler used to handle the workflow request.
     */
    private RollbackHandler rollbackHandler;

    /**
     * Creates a new instance of DecisionHandler.
     * 
     * 
     * @param candidateEndpoints
     *            The candidates for the decision. One of the candidates must be
     *            chosen.
     * @param callbackOperationHandler
     *            An instance of OperationHandlerImpl that handles the callback
     *            of the decision handler.
     * @param wsaSoapMessage
     *            The request by the workflow
     * @param rollbackHandler
     *            The rollbackhandler used to handle the workflow request.
     */
    DecisionHandler(List<Endpoint> candidateEndpoints,
	    OperationHandlerImpl callbackOperationHandler, WsaSoapMessage wsaSoapMessage,
	    RollbackHandler rollbackHandler) {

	this.candidateEndpoints = candidateEndpoints;
	this.callbackOperationHandler = callbackOperationHandler;
	this.wsaSoapMessage = wsaSoapMessage;
	this.rollbackHandler = rollbackHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
	String requestId = UUID.randomUUID().toString();

	// create sender and handle error
	Callable<Map<String, String>> sender = createPluginSender(requestId);
	if (sender == null) {
	    logger.warn("Could not send decision request, because plugin is not available");
	    callbackOperationHandler.decisionCallback(-1, wsaSoapMessage, rollbackHandler);
	    return;
	}

	// create callback http endpoint and handle error
	if (!createCallbackEndpoint(requestId)) {
	    logger.warn("Could not create callback endpoint");
	    callbackOperationHandler.decisionCallback(-1, wsaSoapMessage, rollbackHandler);
	    return;
	}

	// send request and wait for answer if successful
	if (!sendDecisionRequest(sender)) {
	    logger.warn("Could not send message to decider.");
	    callbackOperationHandler.decisionCallback(-1, wsaSoapMessage, rollbackHandler);
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
     * Creates a Callable that can be used to send the decision request. Uses
     * the plugin system to do this. The android plugin is required.
     * 
     * @param requestId
     *            the id of the decision request
     * @return the callable or null if the creation of the callable failed.
     */
    private Callable<Map<String, String>> createPluginSender(String requestId) {
	// use android plugin to send message. Create instance of plugin
	PluginParams pluginParams = new PluginParams();
	pluginParams.setParam("type", "decision");

	pluginParams.setParam("requestId", requestId);
	StringBuilder choicesString = new StringBuilder();
	Iterator<Endpoint> choiceIterator = candidateEndpoints.iterator();

	// concat choices as string
	while (choiceIterator.hasNext()) {
	    Endpoint candidate = choiceIterator.next();
	    choicesString.append(candidate.getEndpointID());
	    choicesString.append("$");
	    choicesString.append(candidate.getEndpointName());
	    choicesString.append("$");
	    choicesString.append(candidate.getEndpointDescription());
	    if (choiceIterator.hasNext()) {
		choicesString.append("$");
	    }
	}

	pluginParams.setParam("choices", choicesString.toString());

	return PluginManagerFactory.getPluginManager().getPluginSender("situationHandler.android",
		wsaSoapMessage.getDecider(), wsaSoapMessage.getDecisionDescription(), pluginParams);
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

	DecisionHandler thisHandler = this;

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
		DecisionAnswer answer = mapper.readValue(answerString, DecisionAnswer.class);

		int answerEndpointId = (answer == null) ? -1 : Integer.parseInt(answer.getChoice());
		callbackOperationHandler.decisionCallback(answerEndpointId, wsaSoapMessage,
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
     * Uses the callable to send the request to the decider. Uses the camel
     * thread pool.
     * 
     * @param sender
     *            the callable to send the request.
     * @return true, if the request was successfully sent, false else.
     */
    private boolean sendDecisionRequest(Callable<Map<String, String>> sender) {
	try {
	    Map<String, String> result = CamelUtil.getCamelExecutorService().submit(sender).get();
	    return Boolean.parseBoolean(result.get("success"));
	} catch (InterruptedException | ExecutionException e) {
	    return false;
	}
    }
}

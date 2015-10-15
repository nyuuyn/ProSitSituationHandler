/**
 * 
 */
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
 * TODO
 * 
 * @author Stefan
 *
 */
class DecisionResultHandler implements Runnable {

    /** The logger. */
    private static final Logger logger = Logger.getLogger(MessageRouter.class);

    private List<Endpoint> candidateEndpoints;

    private OperationHandlerImpl callbackOperationHandler;

    private WsaSoapMessage wsaSoapMessage;

    private RollbackHandler rollbackHandler;

    /**
     * @param callbackOperationHandler
     * @param wsaSoapMessage
     * @param rollbackHandler
     */
    DecisionResultHandler(List<Endpoint> candidateEndpoints,
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
	Callable<Map<String, String>> sender = createPluginSender(requestId);

	if (sender == null) {
	    logger.warn("Could not send decision request, because plugin is not available");
	    callbackOperationHandler.decisionCallback(-1, wsaSoapMessage, rollbackHandler);
	    return;
	}

	if (!createCallbackEndpoint(requestId)) {
	    logger.warn("Could not create callback endpoint");
	    callbackOperationHandler.decisionCallback(-1, wsaSoapMessage, rollbackHandler);
	    return;
	}

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
	try {
	    CamelUtil.getCamelContext().stopRoute(requestId);
	    CamelUtil.getCamelContext().removeRoute(requestId);
	} catch (Exception e) {
	    logger.error("Could not stop/remove route.", e);
	}

    }

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
	    if (choiceIterator.hasNext()) {
		choicesString.append("$");
	    }
	}

	pluginParams.setParam("choices", choicesString.toString());

	return PluginManagerFactory.getPluginManager().getPluginSender("situationHandler.android",
		wsaSoapMessage.getDecider(), wsaSoapMessage.getDecisionDescription(), pluginParams);
    }

    private boolean createCallbackEndpoint(String requestId) {
	String camelComponent = SituationHandlerProperties.getHttpEndpointComponent();
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

	DecisionResultHandler thisHandler = this;

	// create processor
	Processor resultHandler = new Processor() {
	    @Override
	    public void process(Exchange exchange) throws Exception {
		String answerString = exchange.getIn().getBody(String.class);
		ObjectMapper mapper = new ObjectMapper();
		DecisionAnswer answer = mapper.readValue(answerString, DecisionAnswer.class);

		int answerEndpointId = (answer == null) ? -1 : Integer.parseInt(answer.getChoice());
		callbackOperationHandler.decisionCallback(answerEndpointId, wsaSoapMessage,
			rollbackHandler);

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

    private boolean sendDecisionRequest(Callable<Map<String, String>> sender) {
	try {
	    Map<String, String> result = CamelUtil.getCamelExecutorService().submit(sender).get();
	    return Boolean.parseBoolean(result.get("success"));
	} catch (InterruptedException | ExecutionException e) {
	    return false;
	}
    }
}

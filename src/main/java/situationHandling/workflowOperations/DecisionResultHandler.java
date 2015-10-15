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

import org.apache.log4j.Logger;

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
	// use android plugin to send message
	PluginParams pluginParams = new PluginParams();
	pluginParams.setParam("type", "decision");
	String requestId = UUID.randomUUID().toString();
	pluginParams.setParam("requestId", requestId);
	StringBuilder choicesString = new StringBuilder();
	Iterator<Endpoint> choiceIterator = candidateEndpoints.iterator();

	while (choiceIterator.hasNext()) {
	    Endpoint candidate = choiceIterator.next();
	    choicesString.append(candidate.getEndpointID());
	    if (choiceIterator.hasNext()) {
		choicesString.append("$");
	    }
	}

	pluginParams.setParam("choices", choicesString.toString());
	Callable<Map<String, String>> sender = PluginManagerFactory.getPluginManager()
		.getPluginSender("situationHandler.android", wsaSoapMessage.getDecider(),
			wsaSoapMessage.getDecisionDescription(), pluginParams);
	try {
	    Map<String, String> result = CamelUtil.getCamelExecutorService().submit(sender).get();
	    if (result.get("success").equals("false")) {
		logger.warn("Could not send message to decider.");
		callbackOperationHandler.decisionCallback(-1, wsaSoapMessage, rollbackHandler);
		return;
	    }
	} catch (InterruptedException | ExecutionException e) {
	    logger.warn("Could not send message to decider.");
	    callbackOperationHandler.decisionCallback(-1, wsaSoapMessage, rollbackHandler);
	    return;
	}
	

	// TODO: beide Pfade!
	DecisionAnswer answer = CamelUtil.getConsumerTemplate().receiveBody(
		"jetty:http://0.0.0.0:8081/situationhandler/config/decisions/" + requestId, 600_000,
		DecisionAnswer.class);
	if (answer != null) {
	    callbackOperationHandler.decisionCallback(Integer.parseInt(answer.getChoice()),
		    wsaSoapMessage, rollbackHandler);
	} else {
	    callbackOperationHandler.decisionCallback(-1, wsaSoapMessage, rollbackHandler);
	}
    }

}

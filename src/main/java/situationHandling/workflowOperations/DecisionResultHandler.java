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
	// TODO: fehlendes Plugin abfangen und route vor dem abschicken erzeugen
	PluginParams pluginParams = new PluginParams();
	pluginParams.setParam("type", "decision");
	String requestId = UUID.randomUUID().toString();
	System.out.println("RequestID: " + requestId);
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
	if (sender == null){
	    logger.warn("Could not send decision request, because plugin is not available");
	    callbackOperationHandler.decisionCallback(-1, wsaSoapMessage, rollbackHandler);
	}

	// TODO: beide Pfade!
	// TODO: Das config aus dem Pfad rausnehmen
	String path = "jetty:http://0.0.0.0:8081/situationhandler/decisions/" + requestId;
	System.out.println("Receiving on path: " + path);

	DecisionResultHandler thisHandler = this;

	Processor resultHandler = new Processor() {

	    @Override
	    public void process(Exchange exchange) throws Exception {
		String answerString = exchange.getIn().getBody(String.class);
		ObjectMapper mapper = new ObjectMapper();
		DecisionAnswer answer = mapper.readValue(answerString, DecisionAnswer.class);
		System.out.println("Answer: " + answer);
		if (answer != null) {
		    callbackOperationHandler.decisionCallback(Integer.parseInt(answer.getChoice()),
			    wsaSoapMessage, rollbackHandler);
		} else {
		    callbackOperationHandler.decisionCallback(-1, wsaSoapMessage, rollbackHandler);
		}
		synchronized (thisHandler) {
		    thisHandler.notify();
		}
	    }
	};
	try {
	    CamelUtil.getCamelContext().addRoutes(new DynamicEndpointBuilder(
		    CamelUtil.getCamelContext(), path, resultHandler, requestId));
	} catch (Exception e) {
	    e.printStackTrace();
	}

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

	System.out.println("Waitung for answer....");

	synchronized (this) {
	    try {
		this.wait();
	    } catch (InterruptedException e) {

	    }
	}
	try {
	    CamelUtil.getCamelContext().stopRoute(requestId);
	    CamelUtil.getCamelContext().removeRoute(requestId);
	    System.out.println("Route removed");
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}

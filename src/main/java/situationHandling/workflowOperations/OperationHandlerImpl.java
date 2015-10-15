package situationHandling.workflowOperations;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.soap.SOAPConstants;

import org.apache.log4j.Logger;

import main.CamelUtil;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;
import situationManagement.SituationManager;
import situationManagement.SituationManagerFactory;
import utils.soap.SoapRequestFactory;
import utils.soap.WsaSoapMessage;

/**
 * The Class OperationHandlerImpl provides the implementation of
 * {@link OperationHandlerForRollback}.
 * <p>
 * It is primarily used to determine an appropriate endpoint to execute an
 * operation. The requests and answers are forwarded to the correct receiver. If
 * no endpoint is found or another error occurs, it initiates the sending of a
 * fault message.
 * <p>
 * Furthermore, there is tight cooperation with a rollback manager to ensure
 * appropriate rollback handling.
 */
class OperationHandlerImpl implements OperationHandlerForRollback {

    /** The logger for this class. */
    private final static Logger logger = Logger.getLogger(OperationHandlerImpl.class);

    /** The rollback manager. */
    private RollbackManager rollbackManager;

    /**
     * Instantiates a new operation handler implementation.
     *
     * @param rollbackManager
     *            the rollback manager to use
     */
    OperationHandlerImpl(RollbackManager rollbackManager) {
	this.rollbackManager = rollbackManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * situationHandling.workflowOperations.OperationHandler#handleOperation(
     * utils.soap.WsaSoapMessage)
     */
    @Override
    public void handleOperation(WsaSoapMessage wsaSoapMessage) {
	handleOperation(wsaSoapMessage, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * situationHandling.workflowOperations.OperationHandler#handleOperation(
     * utils.soap.WsaSoapMessage,
     * situationHandling.workflowOperations.RollbackHandler)
     */
    @Override
    public void handleOperation(WsaSoapMessage wsaSoapMessage, RollbackHandler rollbackHandler) {

	String operationName;
	String qualifier;
	// wsa action is used as operation if specified, else ns + name
	if (wsaSoapMessage.getWsaAction() != null && !wsaSoapMessage.getWsaAction().equals("")) {
	    operationName = wsaSoapMessage.getWsaAction();
	    qualifier = "wsa:Action";
	} else {
	    operationName = wsaSoapMessage.getOperationName();
	    qualifier = wsaSoapMessage.getNamespace();
	}

	logger.debug("Handling Operation: " + operationName + ":" + qualifier);
	List<Endpoint> bestEndpoints = chooseEndpoint(new Operation(operationName, qualifier));
	// either there is no endpoint (error), a definite endpoint (only one or
	// no decider) or more that one endpoint with a decider specified
	if (bestEndpoints.size() == 0) {
	    logger.warn("No endpoint found for Operation: " + operationName + ":" + qualifier);
	    sendErrorMessage("No endpoint found for " + operationName + ":" + qualifier
		    + ". Nothing executed.", wsaSoapMessage);
	    return;
	} else if (bestEndpoints.size() == 1 || wsaSoapMessage.getDecider() == null) {
	    sendToEndpoint(bestEndpoints.get(0), wsaSoapMessage, rollbackHandler);
	} else {
	    // contact decider
	    CamelUtil.getCamelExecutorService().submit(new DecisionResultHandler(bestEndpoints,
		    this, wsaSoapMessage, rollbackHandler));
	}
    }

    /**
     * Callback for decision result handlers. Initiates further processing.
     * 
     * @param resultEndpointId
     *            the decision (endpoint) when a decision was made. Else -1.
     * @param wsaSoapMessage
     *            the request this decision relates to
     * @param rollbackHandler
     *            the rollbackhandler (if available)
     */
    public void decisionCallback(int resultEndpointId, WsaSoapMessage wsaSoapMessage,
	    RollbackHandler rollbackHandler) {
	Endpoint resultEndpoint;
	// if invalid result, sent error message, else forward request
	if (resultEndpointId == -1 || (resultEndpoint = StorageAccessFactory
		.getEndpointStorageAccess().getEndpointByID(resultEndpointId)) == null) {
	    sendErrorMessage("Decision was requested but not result was received.", wsaSoapMessage);
	} else {
	    sendToEndpoint(resultEndpoint, wsaSoapMessage, rollbackHandler);
	}
    }

    /**
     * Helper method to send an error message that was caused when processing a
     * request.
     *
     * @param error
     *            the error message
     * @param request
     *            the message that caused the error
     */
    private void sendErrorMessage(String error, WsaSoapMessage request) {
	// use the correlation id if specified or the message id else
	String correlationId = request.getFaultCorrelationId() == null ? request.getWsaMessageID()
		: request.getFaultCorrelationId();
	WsaSoapMessage rollbackMessage = SoapRequestFactory.createFaultMessageWsa(
		request.getWsaReplyTo().toString(), correlationId, error,
		SOAPConstants.SOAP_RECEIVER_FAULT);

	new MessageRouter(rollbackMessage).forwardFaultMessage(null);
    }

    /**
     * Chooses an endpoint for an operation. The endpoint is chosen by the
     * following criteria:
     * <ul>
     * <li>All Situations associated with the endpoint MUST be fulfilled
     * (logical AND between the situations).</li>
     * <li>Optional Situations do not need to be fulfilled</li>
     * <li>The endpoint with the most matching situations is chosen. So, if aa
     * endpoint specifies three situations and another endpoint specifies only
     * two situations, the endpoint with three situations will be prefered over
     * the endpoint with two situations.</li>
     * <li>Optional situations are lesser weighted than non-optional situations.
     * If an endpoint specifies one situation and another optional situation, an
     * endpoint with two non-optional situations will be prefered.</li>
     * <li>If there are several endpoints to fulfill these criteria, an
     * arbitrary endpoint from these endpoints is chosen.</li>
     * </ul>
     * 
     * @param operation
     *            the operation an endpoint is needed for.
     * @return The endpoint chosen by the criteria listed above or {@code Null}
     *         if no endpoint matching these criteria was found.
     */
    private List<Endpoint> chooseEndpoint(Operation operation) {
	List<Endpoint> candidateEndpoints = StorageAccessFactory.getEndpointStorageAccess()
		.getCandidateEndpoints(operation);
	LinkedList<Endpoint> bestCandidates = new LinkedList<>();
	int bestScore = -1;

	logger.debug("Candidates: \n" + candidateEndpoints.toString());

	for (Endpoint currentCandidate : candidateEndpoints) {
	    logger.debug("Candidate: " + currentCandidate.getEndpointID());
	    int score = 0;
	    for (HandledSituation handledSituation : currentCandidate.getSituations()) {

		SituationManager situationManager = SituationManagerFactory.getSituationManager();
		Situation situation = new Situation(handledSituation.getSituationName(),
			handledSituation.getObjectName());

		if (situationManager.situationOccured(situation) == handledSituation
			.isSituationHolds()) {
		    logger.debug(situation.toString() + " is " + handledSituation.isSituationHolds()
			    + " occured.");
		    score += handledSituation.isOptional() ? 1 : 2;
		} else {
		    if (!handledSituation.isOptional()) {
			logger.debug(situation.toString() + " is "
				+ handledSituation.isSituationHolds() + " not occured --> stop.");
			// abort computation if situation is not fulfilled.
			score = -2;
			break;
		    } else {
			logger.debug(
				situation.toString() + " is " + handledSituation.isSituationHolds()
					+ " not occured but optional.");
		    }
		}
	    }
	    logger.debug("Endpoint " + currentCandidate.getEndpointID() + ": Score " + score);

	    if (score == bestScore) {
		bestCandidates.add(currentCandidate);
		logger.debug("Adding Endpoint to list of best endpoints"
			+ bestCandidates.get(0).getEndpointID() + " with score " + bestScore);
	    } else if (score > bestScore) {
		bestCandidates.clear();
		bestScore = score;
		bestCandidates.add(currentCandidate);
		logger.debug("Choosing Endpoint " + bestCandidates.get(0).getEndpointID()
			+ " with score " + bestScore);
	    }
	}

	logger.debug("Best candidates - Score: " + bestScore + "\n" + bestCandidates.toString());

	return bestCandidates;
    }

    /**
     * Sends the message to the specified endpoint, using the specified
     * rollbackhandler.
     * 
     * @param target
     * @param wsaSoapMessage
     * @param rollbackHandler
     */
    private void sendToEndpoint(Endpoint target, WsaSoapMessage wsaSoapMessage,
	    RollbackHandler rollbackHandler) {
	URL endpointURL = null;
	try {
	    endpointURL = new URL(target.getEndpointURL());
	} catch (MalformedURLException e) {
	    sendErrorMessage("Chosen endpoint is invalid. URL: " + target.getEndpointURL(),
		    wsaSoapMessage);
	    return;
	}
	String surrogate = new MessageRouter(wsaSoapMessage).forwardRequest(endpointURL);
	StorageAccessFactory.getHistoryAccess().appendWorkflowOperationInvocation(target,
		(surrogate != null));
	if (surrogate == null) {
	    sendErrorMessage("Endpoint cannot be reached. " + target, wsaSoapMessage);
	    return;
	}

	rollbackManager.registerRollbackHandler(rollbackHandler, target, wsaSoapMessage, surrogate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * situationHandling.workflowOperations.OperationHandler#situationChanged(
     * situationHandling.storage.datatypes.Situation, boolean)
     */
    @Override
    public void situationChanged(Situation situation, boolean state) {
	logger.debug(situation.toString() + " changed to " + state + ". Check Rollback.");

	rollbackManager.checkRollback(situation, state);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * situationHandling.workflowOperations.OperationHandler#onAnswerReceived(
     * utils.soap.WsaSoapMessage)
     */
    @Override
    public void onAnswerReceived(WsaSoapMessage wsaSoapMessage) {

	if (!rollbackManager.onRollbackAnswered(wsaSoapMessage)) {
	    // in case this is a regular answer (no rollback), just forward it
	    // (if an "old" answer arrives, the forwarding will not succeed)
	    logger.debug("Received regular answer:" + wsaSoapMessage.toString());
	    new MessageRouter(wsaSoapMessage).forwardAnswer();
	}

    }

}

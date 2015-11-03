package situationHandling.workflowOperations;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.soap.SOAPConstants;

import org.apache.log4j.Logger;

import main.CamelUtil;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Endpoint.EndpointStatus;
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
	handleOperation(wsaSoapMessage, null, true);
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
    public void handleOperation(WsaSoapMessage wsaSoapMessage, RollbackHandler rollbackHandler,
	    boolean checkOnlyAvailable) {

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
	Operation toCheck = new Operation(operationName, qualifier);

	// check either archives or available endpoints
	List<Endpoint> bestEndpoints;
	if (checkOnlyAvailable) {
	    bestEndpoints = chooseEndpoint(toCheck, EndpointStatus.available);
	} else {
	    bestEndpoints = chooseEndpoint(toCheck, EndpointStatus.archive);
	}

	// different cases: found endpoint or not? look for available endpoints
	// or archives? decision required or not
	if (bestEndpoints.size() == 0 && checkOnlyAvailable) {
	    // selection of available endpoints failed: check deployment
	    logger.info("No endpoint found for Operation: " + operationName + ":" + qualifier
		    + " . Trying to deploy a new endpoint.");
	    handleOperation(wsaSoapMessage, rollbackHandler, false);
	} else if (bestEndpoints.size() == 0 && !checkOnlyAvailable) {
	    // selection of archive failed: no endpoints available
	    logger.warn("No endpoint found for Operation: " + operationName + ":" + qualifier);
	    sendErrorMessage("No endpoint found for " + operationName + ":" + qualifier
		    + ". Nothing executed.", wsaSoapMessage);
	    return;
	} else if (checkOnlyAvailable
		&& (bestEndpoints.size() == 1 || wsaSoapMessage.getDecider() == null)) {
	    // available endpoint selected and no decision required: send
	    // message to endpoint
	    sendToEndpoint(bestEndpoints.get(0), wsaSoapMessage, rollbackHandler);
	} else if (checkOnlyAvailable
		&& !((bestEndpoints.size() == 1 || wsaSoapMessage.getDecider() == null))) {
	    // several endpoints available: decision required
	    logger.info("Could not decide which endpoint to use. Contacting decider.");
	    // contact decider and wait for answer
	    CamelUtil.getCamelExecutorService().submit(new DecisionHandler(bestEndpoints, this,
		    wsaSoapMessage, rollbackHandler, EndpointStatus.available));
	} else if (!checkOnlyAvailable
		&& (bestEndpoints.size() == 1 || wsaSoapMessage.getDecider() == null)) {
	    // an archive for deployment was found and no decision has to be
	    // made --> deploy
	    CamelUtil.getCamelExecutorService().submit(new DeploymentHandler(this, wsaSoapMessage,
		    rollbackHandler, bestEndpoints.get(0)));
	} else if (!checkOnlyAvailable
		&& !(bestEndpoints.size() == 1 || wsaSoapMessage.getDecider() == null)) {
	    // several archives were found --> decision required
	    CamelUtil.getCamelExecutorService().submit(new DecisionHandler(bestEndpoints, this,
		    wsaSoapMessage, rollbackHandler, EndpointStatus.archive));
	} else {
	    // this should never happen :) :)
	    logger.error("No decision could be made for uncertain reason (this is bad).");
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
	logger.info("Decision received. Using endpoint with id: " + resultEndpointId);
	Endpoint resultEndpoint;
	// if invalid result, sent error message, else forward request
	if (resultEndpointId == -1 || (resultEndpoint = StorageAccessFactory
		.getEndpointStorageAccess().getEndpointByID(resultEndpointId)) == null) {
	    logger.warn("Invalid decision retrieved.");
	    sendErrorMessage("Decision was requested but not result was received.", wsaSoapMessage);
	} else {
	    // check if endpoint is still valid and send request (or deploy it)
	    if (rateEndpoint(resultEndpoint, new HashMap<>()) >= 0) {
		if (resultEndpoint.getEndpointStatus() == EndpointStatus.available) {
		    sendToEndpoint(resultEndpoint, wsaSoapMessage, rollbackHandler);
		} else {
		    CamelUtil.getCamelExecutorService().submit(new DeploymentHandler(this,
			    wsaSoapMessage, rollbackHandler, resultEndpoint));
		}
	    } else {
		logger.info(
			"Valid decision received, but endpoint became invalid. Determine new endpoint");
		handleOperation(wsaSoapMessage, rollbackHandler, true);
	    }
	}
    }

    /**
     * The callback method to be used by deployment handlers, when the
     * deployment of a process archive is finished (or failed)
     * 
     * @param success
     *            true, when the deployment was successful, false else
     * @param wsaSoapMessage
     *            the handled message
     * @param rollbackHandler
     *            the rollback handler
     */
    public void deploymentCallback(boolean success, WsaSoapMessage wsaSoapMessage,
	    RollbackHandler rollbackHandler) {
	if (success) {
	    logger.info("Checking endpoints after deployment.");
	    handleOperation(wsaSoapMessage, rollbackHandler, true);
	} else {
	    sendErrorMessage("Deploying a endpoint failed. Cannot continue processing",
		    wsaSoapMessage);
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
     * arbitrary endpoint from these endpoints is chosen (or a decider is
     * contacted. Depends on the request).</li>
     * </ul>
     * 
     * @param operation
     *            the operation an endpoint is needed for.
     * @return The endpoint chosen by the criteria listed above or {@code Null}
     *         if no endpoint matching these criteria was found.
     */
    private List<Endpoint> chooseEndpoint(Operation operation, EndpointStatus endpointStatus) {
	List<Endpoint> candidateEndpoints = StorageAccessFactory.getEndpointStorageAccess()
		.getCandidateEndpoints(operation, endpointStatus);
	LinkedList<Endpoint> bestCandidates = new LinkedList<>();
	int bestScore = -1;
	logger.debug("Candidates: \n" + candidateEndpoints.toString());

	// we store the state of the situations when we evaluated the endpoint.
	// If the state is changed at the end, we have to start again...
	HashMap<Situation, Boolean> ratedSituationStates = new HashMap<>();

	for (Endpoint currentCandidate : candidateEndpoints) {
	    logger.debug("Candidate: " + currentCandidate.getEndpointID());

	    int score = rateEndpoint(currentCandidate, ratedSituationStates);
	    // situation changed when rating the endpoints. Start again.
	    logger.debug("Endpoint " + currentCandidate.getEndpointID() + ": Score " + score);
	    if (score == -3) {
		logger.info(
			"A relevant situation changed while rating an endpoint. Start again...");
		return chooseEndpoint(operation, endpointStatus);
	    } else if (score == bestScore) {
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

	// if a situation changed, i.e. its state differs from the state that it
	// had when it was rated, the score MUST be computed again to get the
	// best result.
	SituationManager situationManager = SituationManagerFactory.getSituationManager();
	for (Situation toCheck : ratedSituationStates.keySet()) {
	    if (ratedSituationStates.get(toCheck) != situationManager.situationOccured(toCheck)) {
		logger.info(
			"Rating of endpoints finished, but a relevant situation changed meanwhile. Restart rating...");
		return chooseEndpoint(operation, endpointStatus);
	    }
	}

	return bestCandidates;
    }

    /**
     * Rates an endpoint, i.e. determines the score for an endpoint. Considers
     * the states that were used to rate other endpoints.
     * 
     * @param candidate
     *            the endpoint to rate
     * @param states
     *            the states of other endpoints. If no states are available, use
     *            an emtpy hashmap!
     * @return the score of the endpoint. The score is a number > 0. Returns -2,
     *         if an endpoint is not suitable. Returns -3 if a situation change
     *         was recognized.
     */
    private int rateEndpoint(Endpoint candidate, HashMap<Situation, Boolean> states) {
	int score = 0;
	for (HandledSituation handledSituation : candidate.getSituations()) {
	    SituationManager situationManager = SituationManagerFactory.getSituationManager();
	    Situation situation = new Situation(handledSituation.getSituationName(),
		    handledSituation.getObjectId());

	    boolean currentState = situationManager.situationOccured(situation);

	    // if we already used a situation to rate an endpoint, the state
	    // must be the same. So we compare the state here and abort if the
	    // state changed..
	    if (states.containsKey(situation) && states.get(situation) != currentState) {
		logger.debug(situation.toString() + " changed when rating an endpoint");
		return -3;
	    } else {
		states.put(situation, currentState);
	    }

	    if (currentState == handledSituation.isSituationHolds()) {
		logger.debug(situation.toString() + " is " + handledSituation.isSituationHolds()
			+ " occured.");
		score += handledSituation.isOptional() ? 1 : 2;
	    } else {
		if (!handledSituation.isOptional()) {
		    logger.debug(situation.toString() + " is " + handledSituation.isSituationHolds()
			    + " not occured --> stop.");
		    // abort computation if situation is not fulfilled.
		    return -2;
		} else {
		    logger.debug(situation.toString() + " is " + handledSituation.isSituationHolds()
			    + " not occured but optional.");
		}
	    }
	}
	return score;
    }

    /**
     * Sends the message to the specified endpoint, using the specified
     * rollbackhandler.
     * 
     * @param target
     *            the used endpoint
     * @param wsaSoapMessage
     *            the request
     * @param rollbackHandler
     *            the rollbackhandler to use
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

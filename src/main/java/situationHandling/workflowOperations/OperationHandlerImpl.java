package situationHandling.workflowOperations;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.soap.SOAPConstants;

import org.apache.log4j.Logger;

import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;
import situationManagement.SituationManager;
import situationManagement.SituationManagerFactory;
import utils.soap.SoapRequestFactory;
import utils.soap.WsaSoapMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class OperationHandlerImpl.
 */
class OperationHandlerImpl implements OperationHandlerWithRollback {

    /** The logger for this class. */
    private final static Logger logger = Logger.getLogger(OperationHandlerImpl.class);

    /** The rollback manager. */
    private RollbackManager rollbackManager;

    /**
     * Instantiates a new operation handler impl.
     *
     * @param rollbackManager
     *            the rollback manager
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
	Endpoint chosenEndpoint = chooseEndpoint(new Operation(operationName, qualifier));
	if (chosenEndpoint == null) {
	    logger.warn("No endpoint found for Operation: " + operationName + ":" + qualifier);
	    sendErrorMessage("No endpoint found for " + operationName + ":" + qualifier
		    + ". Nothing executed.", wsaSoapMessage);
	    return;
	}

	URL endpointURL = null;
	try {
	    endpointURL = new URL(chosenEndpoint.getEndpointURL());
	} catch (MalformedURLException e) {
	    sendErrorMessage("Chosen endpoint is invalid. URL: " + chosenEndpoint.getEndpointURL(),
		    wsaSoapMessage);
	    return;
	}
	String surrogate = new MessageRouter(wsaSoapMessage).forwardRequest(endpointURL);
	StorageAccessFactory.getHistoryAccess().appendWorkflowOperationInvocation(chosenEndpoint,
		(surrogate != null));
	if (surrogate == null) {
	    sendErrorMessage("Endpoint cannot be reached. " + chosenEndpoint, wsaSoapMessage);
	    return;
	}

	rollbackManager.registerRollbackHandler(rollbackHandler, chosenEndpoint, wsaSoapMessage,
		surrogate);

    }

    /**
     * Send error message.
     *
     * @param error
     *            the error
     * @param message
     *            the message
     */
    private void sendErrorMessage(String error, WsaSoapMessage message) {
	WsaSoapMessage rollbackMessage = SoapRequestFactory.createFaultMessageWsa(
		message.getWsaReplyTo().toString(), message.getWsaMessageID(),
		new Operation(message.getOperationName(), message.getNamespace()), error,
		SOAPConstants.SOAP_RECEIVER_FAULT);

	new MessageRouter(rollbackMessage).forwardFaultMessage(null);
	;
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
    private Endpoint chooseEndpoint(Operation operation) {
	List<Endpoint> candidateEndpoints = StorageAccessFactory.getEndpointStorageAccess()
		.getCandidateEndpoints(operation);
	Endpoint bestCandidate = null;
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

	    if (score >= bestScore) {
		bestCandidate = currentCandidate;
		bestScore = score;
		logger.debug("Choosing Endpoint " + bestCandidate.getEndpointID() + " with score "
			+ bestScore);
	    }
	}

	logger.debug("Best candidate - Score: " + bestScore + "\n" + bestCandidate);

	return bestCandidate;
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

	if (!rollbackManager.rollbackAnswered(wsaSoapMessage)) {
	    // in case this is a regular answer (no rollback), just forward it
	    // (if an "old" answer arrives, the forwarding will not succeed)
	    logger.debug("Received regular answer:" + wsaSoapMessage.toString());
	    new MessageRouter(wsaSoapMessage).forwardAnswer();
	}

    }

}

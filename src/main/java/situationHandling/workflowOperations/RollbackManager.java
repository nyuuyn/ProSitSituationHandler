package situationHandling.workflowOperations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Situation;
import utils.soap.WsaSoapMessage;

/**
 * The Class RollbackManager provides the functionality to initiate rollbacks
 * and to process the results.
 */
class RollbackManager {

    /** The logger for this class. */
    private final static Logger logger = Logger.getLogger(RollbackManager.class);

    /**
     * The Constant DEFAULT_ROLLBACK_MAXIMUM. This value is used as maximum
     * number of retries when a request does not specifiy a value.
     */
    private static final int DEFAULT_ROLLBACK_MAXIMUM = 2;

    /**
     * Stores the rollback handlers, the will be executed when a certain
     * situation changes.
     * <p>
     * Uses the following mapping:<br>
     * &lt;Situation, all handlers that must run when this situation changes&gt;
     */
    private HashMap<Situation, LinkedList<RollbackHandler>> rollbackHandlers = new HashMap<>();

    /**
     * Contains the rollback handlers that are currently running, i.e. the
     * rollback has been started but no answer has arrived so far.
     * 
     * &lt;the id of the newly created rollback message, the running handler&gt;
     */
    private HashMap<String, RollbackHandler> runningRollbacks = new HashMap<>();

    /**
     * Checks if a rollback must be executed when the specified situation
     * changes. Initiates all rollbacks that are required.
     *
     * @param situation
     *            the situation that changed
     * @param state
     *            the state to which the situation changed. True, when the
     *            situation appeared, false else
     */
    void checkRollback(Situation situation, boolean state) {
	synchronized (RollbackManager.class) {
	    LinkedList<RollbackHandler> handlers = rollbackHandlers.get(situation);
	    if (handlers == null) {// no handlers registred
		return;
	    }
	    Iterator<RollbackHandler> it = handlers.iterator();
	    while (it.hasNext()) {
		RollbackHandler handler = it.next();
		it.remove();

		String messageID = handler.initiateRollback();
		runningRollbacks.put(messageID, handler);

		StorageAccessFactory.getHistoryAccess()
			.appendWorkflowRollback(handler.getEndpoint(), situation, state);

		// the handler is also removed from all other situations the
		// handler is registred on..(to avoid double rollbacks)
		Iterator<Situation> sitIter = handler.getSituations().iterator();
		while (sitIter.hasNext()) {
		    Situation sitToCheck = sitIter.next();
		    rollbackHandlers.get(sitToCheck).removeFirstOccurrence(handler);
		}
	    }
	}

	printExistingRollbackHandlers();
	printRunningRollbacks();
    }

    /**
     * Register a new rollback handler or an existing rollback handler for
     * execution. After registration, the rollback handler will be executed when
     * a situation that is associated to the handler changes.
     * <p>
     * A rollback handler should be registred, when a request is forwarded to an
     * endpoint.
     * <p>
     * An existing rollback handler should be used when there already was a
     * rollback for a request and this is the n-th time the request is executed.
     * 
     * @param rollbackHandler
     *            an existing rollbackHandler or null when no handler exists
     * @param chosenEndpoint
     *            the chosen endpoint the endpoint to which the request is
     *            forwarded
     * @param wsaSoapMessage
     *            the request
     * @param surrogate
     *            the surrogate id that was assigned to the request
     */
    void registerRollbackHandler(RollbackHandler rollbackHandler, Endpoint chosenEndpoint,
	    WsaSoapMessage wsaSoapMessage, String surrogate) {
	logger.debug("Registering rollback handler for endpoint: " + chosenEndpoint.toString());

	if (rollbackHandler == null) {
	    int rollbackMaximum = wsaSoapMessage.getMaxRetries() != null
		    ? wsaSoapMessage.getMaxRetries() : DEFAULT_ROLLBACK_MAXIMUM;
	    rollbackHandler = new RollbackHandler(chosenEndpoint, rollbackMaximum, wsaSoapMessage,
		    surrogate);
	    logger.trace("New Handler created");
	} else {
	    rollbackHandler.setSurrogateId(surrogate);
	    rollbackHandler.setEndpoint(chosenEndpoint);
	    logger.trace("Reusing existing handler.");
	}

	// add rollback to all rollback situations
	for (Situation sit : chosenEndpoint.getRollbackSituations()) {
	    LinkedList<RollbackHandler> handlers;
	    synchronized (RollbackManager.class) {
		// several threads might at the same time register rollbacks and
		// handle rollbacks for changed situations. Therefore,
		// synchronization is necessary

		if ((handlers = rollbackHandlers.get(sit)) == null) {
		    handlers = new LinkedList<>();
		    rollbackHandlers.put(sit, handlers);
		}
		handlers.add(rollbackHandler);
	    }
	}

	printExistingRollbackHandlers();
    }

    /**
     * This method must be called when an answer for a rollback or a regular
     * answer arrives. It does the cleanup for still existing handlers or
     * initiates the handling of the request.
     * <p>
     * Furthermore, it determines if the answer was a rollback answer or a
     * regular answer.
     *
     * @param answerMessage
     *            the answer message
     * @return true, if the answer was a rollback answer, false else
     */
    boolean onRollbackAnswered(WsaSoapMessage answerMessage) {
	// TODO: Das könnte man auch anders machen: Die Identifikation bezieht
	// sich auf die ID der Nachricht, auf die sich der Rollback bezieht. Das
	// würde es unabhängig vom Header machen und man könnte (auch) über das
	// Feld identfizieren.

	// check if there is a running rollback handler for this message
	RollbackHandler handler = runningRollbacks.remove(answerMessage.getWsaRelatesTo());
	if (handler == null) {// in case that this is not a rollback response
	    // the rollbackhandler that still exists for this message must be
	    // removed!
	    removeRollbackHandler(answerMessage.getWsaRelatesTo());
	    printExistingRollbackHandlers();
	    printRunningRollbacks();
	    return false;
	} else {//in case this is a rollback response
	    logger.debug("Received rollback answer:" + answerMessage.toString());
	    // in case it is a rollback answer, do the appropriate handling
	    handler.onRollbackCompleted(answerMessage);
	    printExistingRollbackHandlers();
	    printRunningRollbacks();
	    return true;
	}
    }

    /**
     * Removes the rollback handler. A rollback handler can be removed when the
     * request was successfully processed.
     * <p>
     * Synchronized to keep tables in sync when more than one threads are used
     * to handle requests.
     * 
     * @param messageId
     *            the id of the rollback message
     */
    private synchronized void removeRollbackHandler(String messageId) {
	for (LinkedList<RollbackHandler> handlers : rollbackHandlers.values()) {
	    Iterator<RollbackHandler> it = handlers.iterator();
	    while (it.hasNext()) {
		RollbackHandler currentHandler = it.next();
		if (currentHandler.getSurrogateId().equals(messageId)) {
		    it.remove();
		}
	    }
	}
    }

    /**
     * Prints the running rollbacks to the log.
     */
    private void printRunningRollbacks() {
	StringBuilder sb = new StringBuilder();
	sb.append("---------Running Rollback Handlers---------\n");
	sb.append("<Rollback message id> --> <Surrogate id of request message>\n");
	for (String s : runningRollbacks.keySet()) {
	    sb.append(s + " --> " + runningRollbacks.get(s).getSurrogateId());
	    sb.append("\n");
	}
	sb.append("---------Running Handlers Finish---------\n");
	logger.trace(sb.toString());
    }

    /**
     * Prints the existing rollback handlers to the log.
     */
    private void printExistingRollbackHandlers() {
	StringBuilder sb = new StringBuilder();
	sb.append("---------Existing Rollback Handlers---------\n");
	sb.append(
		"<Situation> --> <Surrogate ID of request message the handler relates to>, <...>\n");
	for (Situation sit : rollbackHandlers.keySet()) {
	    sb.append(sit + " --> ");
	    for (RollbackHandler rh : rollbackHandlers.get(sit)) {
		sb.append(rh.getSurrogateId() + ", ");
	    }
	    sb.append("\n");
	}
	sb.append("---------Rollback Handlers Finish---------\n");
	logger.trace(sb.toString());
    }
}

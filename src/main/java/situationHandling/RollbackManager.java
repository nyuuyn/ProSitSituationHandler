package situationHandling;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Situation;

class RollbackManager {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(RollbackManager.class);

	private static final int DEFAULT_ROLLBACK_MAXIMUM = 2;

	/**
	 * Manages rollbacks ready to start
	 * 
	 * 
	 * <Situation, all handlers that must run when this situation changes>
	 */
	private HashMap<Situation, LinkedList<RollbackHandler>> rollbackHandlers = new HashMap<>();

	/**
	 * Manages running rollbacks.
	 * 
	 * <the id of the newly created rollback message, the running handler>
	 */
	private HashMap<String, RollbackHandler> runningRollbacks = new HashMap<>();

	void checkRollback(Situation situation, boolean state) {
		synchronized (RollbackManager.class) {
			LinkedList<RollbackHandler> handlers = rollbackHandlers
					.get(situation);
			if (handlers == null) {// no handlers registred
				return;
			}
			Iterator<RollbackHandler> it = handlers.iterator();
			while (it.hasNext()) {
				RollbackHandler handler = it.next();
				it.remove();

				String messageID = handler.initiateRollback();
				runningRollbacks.put(messageID, handler);
				
				StorageAccessFactory.getHistoryAccess().appendWorkflowRollback(handler.getEndpoint(),
						situation, state);

				// the handler is also removed from all other situations the
				// handler is registred on..(to avoid double rollbacks)
				Iterator<Situation> sitIter = handler.getSituations()
						.iterator();
				while (sitIter.hasNext()) {
					Situation sitToCheck = sitIter.next();
					rollbackHandlers.get(sitToCheck).removeFirstOccurrence(
							handler);
				}
			}
		}

		printExistingRollbackHandlers();
		printRunningRollbacks();
	}

	void registerRollbackHandler(RollbackHandler rollbackHandler,
			Endpoint chosenEndpoint, WsaSoapMessage wsaSoapMessage,
			String surrogate) {
		logger.debug("Registering rollback handler for endpoint: "
				+ chosenEndpoint.toString());

		if (rollbackHandler == null) {
			int rollbackMaximum = wsaSoapMessage.getMaxRetries() != null ? wsaSoapMessage
					.getMaxRetries() : DEFAULT_ROLLBACK_MAXIMUM;
			rollbackHandler = new RollbackHandler(chosenEndpoint,
					rollbackMaximum, wsaSoapMessage, surrogate);
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

	boolean rollbackAnswered(WsaSoapMessage wsaSoapMessage) {
		// check if there is a running rollback handler for this message
		RollbackHandler handler = runningRollbacks.remove(wsaSoapMessage
				.getWsaRelatesTo());
		if (handler == null) {
			// the rollbackhandler that still exists for this message must be
			// removed!
			removeRollbackHandler(wsaSoapMessage.getWsaRelatesTo());
			printExistingRollbackHandlers();
			printRunningRollbacks();
			return false;
		} else {
			logger.debug("Received rollback answer:"
					+ wsaSoapMessage.toString());
			// in case it is a rollback answer, do the appropriate handling
			handler.onRollbackCompleted(wsaSoapMessage);
			printExistingRollbackHandlers();
			printRunningRollbacks();
			return true;
		}
	}

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

	private void printExistingRollbackHandlers() {
		StringBuilder sb = new StringBuilder();
		sb.append("---------Existing Rollback Handlers---------\n");
		sb.append("<Situation> --> <Surrogate ID of request message the handler relates to>, <...>\n");
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

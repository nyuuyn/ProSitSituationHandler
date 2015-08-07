package situationHandling;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;
import situationManagement.SituationManager;
import situationManagement.SituationManagerFactory;

class OperationHandlerImpl implements OperationHandler {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(OperationHandlerImpl.class);

	@Override
	public OperationHandlingResult handleOperation(SoapMessage soapMessage,
			String qualifier) {
		String operationName = soapMessage.getOperationName();

		logger.debug("Handling Operation: " + operationName + ":" + qualifier);
		Endpoint chosenEndpoint = chooseEndpoint(new Operation(operationName,
				qualifier));
		if (chosenEndpoint == null) {
			logger.warn("No endpoint found for Operation: " + operationName
					+ ":" + qualifier);
			return OperationHandlingResult.noMatchFound;
		}

		try {
			URL endpointURL = new URL(chosenEndpoint.getEndpointURL());
			boolean success = new MessageRouter(soapMessage)
					.forwardRequest(endpointURL);
			StorageAccessFactory.getHistoryAccess().appendWorkflowOperationInvocation(
					chosenEndpoint, success);
			// TODO: Hier wird gleich ein Fehler Nachricht gesendet!
			if (!success) {
				return OperationHandlingResult.error;
			}

			registerRollbackHandler();

			return OperationHandlingResult.success;
		} catch (MalformedURLException e) {
			return OperationHandlingResult.error;
		}

	}

	@Override
	public void situationChanged(Situation situation, boolean state) {
		logger.debug(situation.toString() + " changed to " + state
				+ ". Check Rollback.");
		//TODO: Hier passt das nicht so ganz, weil ich den Endpunkt nicht kenne!
//		StorageAccessFactory.getHistoryAccess().appendWorkflowRollback(null, situation, state);
		// TODO: Rollback
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
		List<Endpoint> candidateEndpoints = StorageAccessFactory
				.getEndpointStorageAccess().getCandidateEndpoints(operation);
		Endpoint bestCandidate = null;
		int bestScore = -1;

		logger.debug("Candidates: \n" + candidateEndpoints.toString());

		for (Endpoint currentCandidate : candidateEndpoints) {
			logger.debug("Candidate: " + currentCandidate.getEndpointID());
			int score = 0;
			for (HandledSituation handledSituation : currentCandidate
					.getSituations()) {

				SituationManager situationManager = SituationManagerFactory
						.getSituationManager();
				Situation situation = new Situation(
						handledSituation.getSituationName(),
						handledSituation.getObjectName());

				if (situationManager.situationOccured(situation) == handledSituation
						.isSituationHolds()) {
					logger.debug(situation.toString() + " is "
							+ handledSituation.isSituationHolds() + " occured.");
					score += handledSituation.isOptional() ? 1 : 2;
				} else {
					if (!handledSituation.isOptional()) {
						logger.debug(situation.toString() + " is "
								+ handledSituation.isSituationHolds()
								+ " not occured --> stop.");
						// abort computation if situation is not fulfilled.
						score = -2;
						break;
					} else {
						logger.debug(situation.toString() + " is "
								+ handledSituation.isSituationHolds()
								+ " not occured but optional.");
					}
				}
			}
			logger.debug("Endpoint " + currentCandidate.getEndpointID()
					+ ": Score " + score);

			if (score >= bestScore) {
				bestCandidate = currentCandidate;
				bestScore = score;
				logger.debug("Choosing Endpoint "
						+ bestCandidate.getEndpointID() + " with score "
						+ bestScore);
			}
		}

		logger.debug("Best candidate - Score: " + bestScore + "\n"
				+ bestCandidate);

		return bestCandidate;
	}

	// /**
	// *
	// * @param endpoint
	// * @param payload
	// * @return true when successful, false else
	// */
	// private boolean invokeEndpoint(Endpoint endpoint, String payload) {
	// PluginManager pm = PluginManagerFactory.getPluginManager();
	// PluginParams params = new PluginParams();
	//
	// params.setParam("Http method", "POST");
	// Map<String, String> results = null;
	// try {
	// // TODO: Das Exception Handling hier bringt nix --> die exception
	// // muss schon gescheit vom plugin behandelt werden!
	// results = pm.getPluginSender("situationHandler.http",
	// endpoint.getEndpointURL(), payload, params).call();
	// } catch (Exception e) {
	// logger.error("Error when invoking Endpoint.", e);
	// return false;
	// }
	//
	// logger.debug("Success invoking Endpoint. Result: "
	// + results.get("body"));
	//
	// return true;
	// }

	private void registerRollbackHandler() {

	}

}

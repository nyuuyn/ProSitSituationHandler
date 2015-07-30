/**
 * 
 */
package situationHandling.storage;

import java.util.List;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;
import situationManagement.SituationManager;
import situationManagement.SituationManagerFactory;

/**
 * The Class EndpointStorageAccessWithSubscribe implements the
 * {@link EndpointStorageAccess} Interface. It uses another implementation of
 * the Interface to handle the Database access. Additionally, it takes care of
 * the subscriptions on situations when endpoints/situations are
 * added/deleted/updated.
 * 
 * @see EndpointStorageAccessDefaultImpl
 * @see EndpointStorageAccessAdvancedChecks
 *
 * @author Stefan
 */
class EndpointStorageAccessWithSubscribe implements EndpointStorageAccess {

	/** The esa. */
	private EndpointStorageAccess esa;

	/**
	 * Instantiates a new endpoint storage access with subscribe.
	 *
	 * @param esa
	 *            the esa
	 */
	EndpointStorageAccessWithSubscribe(EndpointStorageAccess esa) {
		this.esa = esa;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.EndpointStorageAccess#getCandidateEndpoints
	 * (situationHandling.storage.datatypes.Operation)
	 */
	@Override
	public List<Endpoint> getCandidateEndpoints(Operation operation) {
		return esa.getCandidateEndpoints(operation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.EndpointStorageAccess#getAllEndpoints()
	 */
	@Override
	public List<Endpoint> getAllEndpoints() {
		return esa.getAllEndpoints();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.EndpointStorageAccess#getEndpointByID(int)
	 */
	@Override
	public Endpoint getEndpointByID(int endpointID) {
		return esa.getEndpointByID(endpointID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.EndpointStorageAccess#addEndpoint(situationHandling
	 * .storage.datatypes.Operation, java.util.List, java.lang.String)
	 */
	@Override
	public int addEndpoint(Operation operation,
			List<HandledSituation> situations, String endpointURL)
			throws InvalidEndpointException {

		try {
			int id = esa.addEndpoint(operation, situations, endpointURL);
			// add a subscription for each of the situations
			SituationManager situationManager = SituationManagerFactory
					.getSituationManager();
			for (HandledSituation handledSituation : situations) {
				situationManager.subscribeOnSituation(new Situation(
						handledSituation.getSituationName(), handledSituation
								.getObjectName()));
			}
			return id;
		} catch (InvalidEndpointException e) {
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.EndpointStorageAccess#deleteEndpoint(int)
	 */
	@Override
	public boolean deleteEndpoint(int endpointID) {
		// get handled situations before deleting them (so we know which
		// situations have to be unsubscribed)
		List<HandledSituation> situations = esa
				.getSituationsByEndpoint(endpointID);
		boolean success = deleteEndpoint(endpointID);
		if (success) {
			// delete the subscriptions on situations associated with the
			// endpoint when deletion was successful
			SituationManager situationManager = SituationManagerFactory
					.getSituationManager();
			for (HandledSituation handledSituation : situations) {
				situationManager.removeSubscription(new Situation(
						handledSituation.getSituationName(), handledSituation
								.getObjectName()));
			}

		}
		return success;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.EndpointStorageAccess#updateEndpoint(int,
	 * java.util.List, situationHandling.storage.datatypes.Operation,
	 * java.lang.String)
	 */
	@Override
	public boolean updateEndpoint(int endpointID,
			List<HandledSituation> situations, Operation operation,
			String endpointURL) throws InvalidEndpointException {

		List<HandledSituation> oldSituations = esa
				.getSituationsByEndpoint(endpointID);

		try {
			boolean success = updateEndpoint(endpointID, situations, operation,
					endpointURL);
			if (success) {
				// check for each updated handled situation, if the situation
				// itself changed and update subscriptions if necessary.
				for (HandledSituation handledSituation : situations) {
					HandledSituation oldHandledSituation = null;
					// find old situation with same id
					for (HandledSituation loopSit : oldSituations) {
						if (loopSit.getId() == handledSituation.getId()) {
							oldHandledSituation = loopSit;
						}
					}
					if (oldHandledSituation != null) {
						compareSituationAndUpdateSubscription(
								new Situation(
										oldHandledSituation.getSituationName(),
										oldHandledSituation.getObjectName()),
								new Situation(handledSituation
										.getSituationName(), handledSituation
										.getObjectName()));
					}
				}
			}

			return success;
		} catch (InvalidEndpointException e) {
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.EndpointStorageAccess#updateHandledSituation
	 * (int, situationHandling.storage.datatypes.HandledSituation)
	 */
	@Override
	public boolean updateHandledSituation(int id, HandledSituation newSituation)
			throws InvalidEndpointException {

		// get old situation
		HandledSituation oldHandledSituation = esa.getHandledSituationById(id);
		try {
			boolean success = updateHandledSituation(id, newSituation);
			// only update subscription if update was successful
			if (success) {
				Situation oldSituation = new Situation(
						oldHandledSituation.getSituationName(),
						oldHandledSituation.getObjectName());

				compareSituationAndUpdateSubscription(oldSituation,
						new Situation(newSituation.getSituationName(),
								newSituation.getObjectName()));
			}

			return success;
		} catch (InvalidEndpointException e) {
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.EndpointStorageAccess#deleteHandledSituation
	 * (int)
	 */
	@Override
	public boolean deleteHandledSituation(int id) {

		boolean success = deleteHandledSituation(id);
		if (success) {
			// delete subscription
			SituationManager situationManager = SituationManagerFactory
					.getSituationManager();
			HandledSituation handledSituation = getHandledSituationById(id);
			situationManager.removeSubscription(new Situation(handledSituation
					.getSituationName(), handledSituation.getObjectName()));
		}

		return success;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.EndpointStorageAccess#getHandledSituationById
	 * (int)
	 */
	@Override
	public HandledSituation getHandledSituationById(int id) {
		return getHandledSituationById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.EndpointStorageAccess#addHandledSituation(int,
	 * situationHandling.storage.datatypes.HandledSituation)
	 */
	@Override
	public int addHandledSituation(int endpointId,
			HandledSituation handledSituation) throws InvalidEndpointException {

		try {
			int id = addHandledSituation(endpointId, handledSituation);
			// subscribe on new situation
			SituationManager situationManager = SituationManagerFactory
					.getSituationManager();
			situationManager.subscribeOnSituation(new Situation(
					handledSituation.getSituationName(), handledSituation
							.getObjectName()));

			return id;
		} catch (InvalidEndpointException e) {
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.EndpointStorageAccess#getSituationsByEndpoint
	 * (int)
	 */
	@Override
	public List<HandledSituation> getSituationsByEndpoint(int endpointId) {
		return getSituationsByEndpoint(endpointId);
	}

	/**
	 * Compares two situations. If the situations differ, the subscription on
	 * the old situation is deleted and a subscription on the new situation is
	 * created.
	 * <p>
	 * 
	 * The fields of the properties of {@code oldSituation} can be null. If a
	 * field is null, it is assumed that it did not change compared to the old
	 * situation.
	 * 
	 * @param oldSituation
	 * @param newSituation
	 */
	private void compareSituationAndUpdateSubscription(Situation oldSituation,
			Situation newSituation) {

		String subscriptionSituationName = oldSituation.getSituationName();
		String subscriptionObjectName = oldSituation.getObjectName();

		// check if situation name changed
		boolean changed = false;
		if (newSituation.getSituationName() != null
				&& !newSituation.getSituationName().equals(
						oldSituation.getSituationName())) {
			subscriptionSituationName = newSituation.getSituationName();
			changed = true;
		}
		// check if object name changed
		if (newSituation.getObjectName() != null
				&& newSituation.getObjectName().equals(
						oldSituation.getObjectName())) {
			changed = true;
			subscriptionObjectName = newSituation.getObjectName();

		}
		// update subscription
		if (changed) {
			SituationManager situationManager = SituationManagerFactory
					.getSituationManager();
			situationManager.removeSubscription(oldSituation);
			situationManager.subscribeOnSituation(new Situation(
					subscriptionSituationName, subscriptionObjectName));
		}
	}

}

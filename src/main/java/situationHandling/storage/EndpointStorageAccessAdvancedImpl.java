package situationHandling.storage;

import java.util.List;

import org.hibernate.SessionFactory;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.Operation;

/**
 * 
 * Implements the {@link EndpointStorageAccess}. Does pretty much the same than
 * {@link EndpointStorageAccessDefaultImpl} but does more checks on the validity
 * of the input.
 * 
 * @author Stefan
 *
 */
class EndpointStorageAccessAdvancedImpl extends
		EndpointStorageAccessDefaultImpl {

	/**
	 * Instantiates a new endpoint storage access advanced impl.
	 *
	 * @param sessionFactory
	 *            The session factory used to create database sessions.
	 */
	public EndpointStorageAccessAdvancedImpl(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.EndpointStorageAccessDefaultImpl#addEndpoint
	 * (situationHandling.storage.datatypes.Operation,
	 * situationHandling.storage.datatypes.Situation, java.lang.String)
	 */
	@Override
	public int addEndpoint(Operation operation, List<HandledSituation> situations,
			String endpointURL) throws InvalidEndpointException {
		//TODO: Check ob mind eine Aktion angegeben (bei Update auch)
		new EndpointValidityChecker(endpointURL, situations).checkBeforeAdd();

		return super.addEndpoint(operation, situations, endpointURL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.EndpointStorageAccessDefaultImpl#updateEndpoint
	 * (int, situationHandling.storage.datatypes.Situation,
	 * situationHandling.storage.datatypes.Operation, java.lang.String)
	 */
	@Override
	public boolean updateEndpoint(int endpointID, List<HandledSituation> situations,
			Operation operation, String endpointURL)
			throws InvalidEndpointException {
		new EndpointValidityChecker(endpointURL, null).checkBeforeUpdate();

		return super.updateEndpoint(endpointID, situations, operation,
				endpointURL);
	}

}

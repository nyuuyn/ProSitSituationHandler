package situationHandling.storage;

import org.hibernate.SessionFactory;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

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
	public int addEndpoint(Operation operation, Situation situation,
			String endpointURL) throws InvalidEndpointException {
		new EndpointValidityChecker(endpointURL).checkBeforeAdd();

		return super.addEndpoint(operation, situation, endpointURL);
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
	public boolean updateEndpoint(int endpointID, Situation situation,
			Operation operation, String endpointURL)
			throws InvalidEndpointException {
		new EndpointValidityChecker(endpointURL).checkBeforeUpdate();

		return super.updateEndpoint(endpointID, situation, operation,
				endpointURL);
	}

}

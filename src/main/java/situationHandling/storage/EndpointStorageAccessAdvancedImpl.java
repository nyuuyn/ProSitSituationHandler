package situationHandling.storage;

import org.hibernate.SessionFactory;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

/**
 * @author Stefan
 *
 */
class EndpointStorageAccessAdvancedImpl extends
		EndpointStorageAccessDefaultImpl {

	/**
	 * @param sessionFactory
	 */
	public EndpointStorageAccessAdvancedImpl(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public int addEndpoint(Operation operation, Situation situation,
			String endpointURL) throws InvalidEndpointException {
		new EndpointValidityChecker(endpointURL).checkBeforeAdd();

		return super.addEndpoint(operation, situation, endpointURL);
	}

	@Override
	public boolean updateEndpoint(int endpointID, Situation situation,
			Operation operation, String endpointURL)
			throws InvalidEndpointException {
		new EndpointValidityChecker(endpointURL).checkBeforeUpdate();

		return super.updateEndpoint(endpointID, situation, operation,
				endpointURL);
	}

}

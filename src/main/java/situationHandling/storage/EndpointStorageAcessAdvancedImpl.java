package situationHandling.storage;

import java.net.URL;

import org.hibernate.SessionFactory;

import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

/**
 * @author Stefan
 *
 */
class EndpointStorageAcessAdvancedImpl extends EndpointStorageAccessDefaultImpl {

	/**
	 * @param sessionFactory
	 */
	public EndpointStorageAcessAdvancedImpl(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public int addEndpoint(Operation operation, Situation situation,
			String endpointURL) {
		return super.addEndpoint(operation, situation, endpointURL);
	}

	@Override
	public boolean updateEndpoint(int endpointID, Situation situation,
			Operation operation, String endpointURL) {
		return super.updateEndpoint(endpointID, situation, operation, endpointURL);
	}
	
	

}

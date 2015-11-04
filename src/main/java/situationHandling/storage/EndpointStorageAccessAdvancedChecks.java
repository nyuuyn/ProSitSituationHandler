package situationHandling.storage;

import java.util.List;

import org.hibernate.SessionFactory;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Endpoint.EndpointStatus;

/**
 * 
 * Implements the {@link EndpointStorageAccess}. Does pretty much the same than
 * {@link EndpointStorageAccessDefaultImpl} but does more checks on the validity
 * of the input.
 * 
 * @author Stefan
 *
 */
class EndpointStorageAccessAdvancedChecks extends EndpointStorageAccessDefaultImpl {

    /**
     * Instantiates a new endpoint storage access advanced impl.
     *
     * @param sessionFactory
     *            The session factory used to create database sessions.
     */
    public EndpointStorageAccessAdvancedChecks(SessionFactory sessionFactory) {
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
    public int addEndpoint(String endpointName, String endpointDescription, Operation operation,
	    List<HandledSituation> situations, String endpointURL, String archiveFilename,
	    EndpointStatus endpointStatus) throws InvalidEndpointException {

	new EndpointValidityChecker(-1, endpointURL, archiveFilename, endpointStatus)
		.checkBeforeAdd();

	return super.addEndpoint(endpointName, endpointDescription, operation, situations,
		endpointURL, archiveFilename, endpointStatus);
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
    public boolean updateEndpoint(int endpointID, String endpointName, String endpointDescription,
	    List<HandledSituation> situations, Operation operation, String endpointURL,
	    String archiveFilename, EndpointStatus endpointStatus) throws InvalidEndpointException {

	new EndpointValidityChecker(endpointID, endpointURL, archiveFilename, endpointStatus)
		.checkBeforeUpdate();
	return super.updateEndpoint(endpointID, endpointName, endpointDescription, situations,
		operation, endpointURL, archiveFilename, endpointStatus);
    }

}

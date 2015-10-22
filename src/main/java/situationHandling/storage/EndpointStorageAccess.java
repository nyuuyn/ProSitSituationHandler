package situationHandling.storage;

import java.util.List;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

/**
 * The Interface {@codeEndpointStorageAccess} gives access to the endpoint
 * directory. Main purpose of the endpoint storage/directory is to provide an
 * endpoint that offers an certain operation depending on the current situation.
 * Furthermore manipulation of the list of existing endpoints is possible.
 * <p>
 * This {@code Interface} allows to add, manipulate and retrieve endpoints from
 * the endpoint storage. It defines methods for all interaction with the
 * directory that is required. Instances of Implementing classes can be created
 * using {@link StorageAccessFactory}.
 * 
 * <p>
 * Note that this Interface is only used to access endpoints and not rules. For
 * rules see the {@code Interface} {@link RuleStorageAccess}.
 * 
 * @see Endpoint
 * @see Situation
 * @see Operation
 */
public interface EndpointStorageAccess {

    /**
     * Gets the endpoints that offer the specified operation
     * 
     * @param operation
     *            the operation that should be executed
     * @return A list of endpoints that implement the specified operation.
     *         Returns an empty list if no endpoint was found.
     */
    public List<Endpoint> getCandidateEndpoints(Operation operation);

    /**
     * Gets all endpoints that are currently stored, independent of the
     * situation and operation that are associated with the endpoint.
     *
     * @return all endpoints. An empty list, if no endpoints are available.
     */
    public List<Endpoint> getAllEndpoints();

    /**
     * Gets the endpoint with the given ID.
     * 
     * 
     * @param endpointID
     *            the id of the endpoint
     * @return the endpoint if it exists, null else
     */
    public Endpoint getEndpointByID(int endpointID);

    /**
     * Adds a new endpoint to the endpoint storage/directory. The endpoint will
     * be stored persistently. Furthermore, an unique id is assigned to the
     * endpoint. The id can be used to refer to the endpoint after it was added.
     * <p>
     * Note that if one of the submitted situations is invalid, the endpoint
     * will NOT be added.
     * 
     * @param endpointName
     *            the name of the endpoint.
     * @param endpointDescription
     *            the description of the endpoint
     * @param operation
     *            the operation that is implemented by this endpoint
     * @param endpointURL
     *            the endpoint url
     * @param situations
     *            the situations handled by this endpoint
     * @return the id that was assigned to the endpoint.
     * 
     * @throws InvalidEndpointException
     *             When the specified Endpoint is not valid
     */
    public int addEndpoint(String endpointName, String endpointDescription, Operation operation,
	    List<HandledSituation> situations, String endpointURL) throws InvalidEndpointException;

    /**
     * Deletes the endpoint with the given id from the directory, so it will not
     * be used for situation handling any longer.
     * 
     * <p>
     * If no endpoint with this id exists, nothing happens.
     *
     * @param endpointID
     *            the endpoint id
     * @return {@code true}, if the deletion was successful, {@code false} if
     *         not
     */
    public boolean deleteEndpoint(int endpointID);

    /**
     * Updates an existing endpoint with the specified id. It is possible to
     * update the situations in which the endpoint is used, the operation to use
     * and also the URL.
     * <p>
     * When the handled situations are updated, note that it is required to
     * state at least the id of the situation(s) to update. Otherwise the update
     * of the situation is not possible. However, all other attributes are
     * optional.
     * <p>
     * Note that all parameters except {@code endpointID} are optional. If no
     * endpoint with this id exists, nothing happens.
     *
     * @param endpointID
     *            the endpoint id that is used to uniquely identify an endpoint.
     * 
     * @param operation
     *            the new operation for this endpoint. If {@code operation} is
     *            {@code null}, the operation will not be updated
     * @param endpointURL
     *            the new endpoint url for this endpoint. If {@code endpointURL}
     *            is {@code null}, the endpoint url will not be updated
     * @param situations
     *            the situations to update.
     * @return {@code true}, if the update was successful, {@code false} else
     * 
     * @throws InvalidEndpointException
     *             When the specified Endpoint is not valid
     */
    public boolean updateEndpoint(int endpointID, List<HandledSituation> situations,
	    Operation operation, String endpointURL) throws InvalidEndpointException;

    /**
     * Updates an existing situation with the specified id. It is possible to
     * update properties of the situation (except the endpoint it is associated
     * with).
     * 
     * @param id
     *            the id of the situation to update. If no situation with this
     *            id exists, nothing happens.
     * @param newSituation
     *            The new Situation. Note that all properties of
     *            {@code newSituation} are optional, i.e. they can be null. If a
     *            property is null, this property will remain unchanged.
     * @return true if the update was successful. False if no situation with
     *         this id exists.
     * @throws InvalidEndpointException
     *             If an error occurred during update.
     */
    public boolean updateHandledSituation(int id, HandledSituation newSituation)
	    throws InvalidEndpointException;

    /**
     * Delete the situation with the specified id. It won't be used with its
     * endpoint any longer.
     * 
     * @param id
     *            the id of the situation to delete
     * @return true if the deletion was successful, false else (probably no
     *         situation with this id exists)
     */
    public boolean deleteHandledSituation(int id);

    /**
     * Gets the Situation with the given ID
     * 
     * @param id
     *            the id of the requested
     * @return the Situation if found. Null, if the situation does not exist.
     */
    public HandledSituation getHandledSituationById(int id);

    /**
     * Add a new situation to handle for an existing endpoint.
     * 
     * @param endpointId
     *            the endpoint this situation is added to.
     * @param handledSituation
     *            The situation to add.
     * @return the id of the newly addded situation
     * @throws InvalidEndpointException
     *             when the new situation or the endpoint is invalid.
     */
    public int addHandledSituation(int endpointId, HandledSituation handledSituation)
	    throws InvalidEndpointException;

    /**
     * Get all situations handled by an specific endpoint.
     * 
     * @param endpointId
     *            the id of the endpoint.
     * @return the situations handled by the endpoint. An empty list if no
     *         situations are handled. {@code Null} if the endpoint does not
     *         exist.
     */
    public List<HandledSituation> getSituationsByEndpoint(int endpointId);
}

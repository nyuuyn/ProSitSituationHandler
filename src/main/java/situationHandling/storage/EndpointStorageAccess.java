package situationHandling.storage;

import java.net.URL;
import java.util.List;

import situationHandling.storage.datatypes.Endpoint;
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

	// TODO: Default Endpunkt oder Null returnen, falls kein passender Endpunkt
	// gefunden?? -->Klären
	/**
	 * Gets the endpoint url of an endpoint that offers the specified operation
	 * and that suits the specified situation. SOAP Messages can be sent to this
	 * endpoint.
	 *
	 * @param situation
	 *            the situation that currently holds
	 * @param operation
	 *            the operation that should be executed
	 * @return the url of the endpoint that provides an appropriate
	 *         implementation of the specified operation depending on the
	 *         specified situation. Returns {@code Null} if no endpoint was
	 *         found.
	 */
	public URL getEndpointURL(Situation situation, Operation operation);

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
	 *
	 * @param operation
	 *            the operation that is implemented by this endpoint
	 * @param situation
	 *            the situation in which this endpoint should be used
	 * @param endpointURL
	 *            the endpoint url
	 * @return the id that was assigned to the endpoint.
	 */
	public int addEndpoint(Operation operation, Situation situation,
			URL endpointURL);

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
	 * update the situation in which the endpoint is used, the operation to use
	 * and also the URL.
	 * <p>
	 * Note that all parameters except {@code endpointID} are optional. If no
	 * endpoint with this id exists, nothing happens.
	 *
	 * @param endpointID
	 *            the endpoint id that is used to uniquely identify an endpoint.
	 * @param situation
	 *            the new situation for this endpoint. If {@code situation} is
	 *            {@code null}, the situation will not be updated
	 * @param operation
	 *            the new operation for this endpoint. If {@code operation} is
	 *            {@code null}, the operation will not be updated
	 * @param endpointURL
	 *            the new endpoint url for this endpoint. If {@code endpointURL}
	 *            is {@code null}, the endpoint url will not be updated
	 * @return {@code true}, if the update was successful, {@code false} else
	 */
	public boolean updateEndpoint(int endpointID, Situation situation,
			Operation operation, URL endpointURL);
}

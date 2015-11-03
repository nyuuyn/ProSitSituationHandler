package restApiImpl;

import java.util.LinkedList;
import java.util.List;

import org.apache.camel.Exchange;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.EndpointStorageAccess;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;

/**
 * The Class EndpointAPI implements the functionality of the rest configuration
 * api for the endpoints. For each allowed rest-operation, there is a dedicated
 * operation.
 * <p>
 * The class serves as target for the camel route that specifies the rest api
 * methods.
 * 
 * @see Endpoint
 * @see EndpointStorageAccess
 */
public class EndpointAPI {

    /** The instance of {@code EndpointStorageAccess} to access the storage. */
    private EndpointStorageAccess esa;

    /**
     * Creates a new instance of EndpointApi and does necessary configuration.
     */
    public EndpointAPI() {
	esa = StorageAccessFactory.getEndpointStorageAccess();
    }

    /**
     * Gets all endpoints that are currently available.
     * <p>
     * Target method for a camel route. The exchange is created by camel.
     * 
     * @param exchange
     *            the exchange that contains the received message. Also serves
     *            as container for the answer.
     * @return The endpoints as list. If there are no endpoints, an empty list
     *         is returned. The return value is stored in the exchange.
     */
    public void getEndpoints(Exchange exchange) {
	exchange.getIn().setBody(esa.getAllEndpoints());
    }

    /**
     * Adds the endpoint to the directory.
     * <p>
     * Target method for a camel route. The exchange is created by camel.
     * 
     * @param exchange
     *            the exchange that contains the received message. Must contain
     *            an instance of {@link Endpoint} in the body. Also serves as
     *            container for the answer.
     * @return The id of the new endpoint. An 422-Error if the endpoint is
     *         invalid. The return value is stored in the exchange.
     */
    public void addEndpoint(Exchange exchange) {
	Endpoint endpoint = exchange.getIn().getBody(Endpoint.class);

	try {
	    List<HandledSituation> situations = endpoint.getSituations();
	    int endpointID = esa.addEndpoint(endpoint.getEndpointName(),
		    endpoint.getEndpointDescription(), endpoint.getOperation(),
		    (situations == null ? new LinkedList<>() : situations),
		    endpoint.getEndpointURL(), endpoint.getArchiveFilename(),
		    endpoint.getEndpointStatus());
	    exchange.getIn().setBody(
		    new RestAnswer("Endpoint successfully added.", String.valueOf(endpointID)));
	} catch (InvalidEndpointException e) {
	    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	    exchange.getIn().setBody(e.getMessage());
	    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
	}
    }

    /**
     * Gets an endpoint by id from the directory.
     * <p>
     * Target method for a camel route. The exchange is created by camel.
     * 
     * @param endpointID
     *            the endpoint id
     * 
     * @param exchange
     *            the exchange that contains the received message. Also serves
     *            as container for the answer.
     * @return The endpoint. If there is no endpoint with this id a 404-error is
     *         returned. The return value is stored in the exchange.
     */
    public void getEndpointByID(Integer endpointID, Exchange exchange) {
	Endpoint endpoint = esa.getEndpointByID(endpointID);
	if (endpoint == null) {
	    exchange.getIn().setBody("Endpoint with id " + endpointID + " not found.");
	    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
	    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	} else {
	    exchange.getIn().setBody(endpoint);
	}
    }

    /**
     * Deletes the endpoint with the given id from the directory.
     * <p>
     * Target method for a camel route. The exchange is created by camel.
     * 
     * @param endpointID
     *            the endpoint id
     * @param exchange
     *            the exchange that contains the received message. Also serves
     *            as container for the answer.
     * @return A 404-error, if there is no endpoint with the given id.
     */
    public void deleteEndpoint(Integer endpointID, Exchange exchange) {
	if (esa.deleteEndpoint(endpointID)) {
	    exchange.getIn().setBody(
		    new RestAnswer("Endpoint Successfully deleted", String.valueOf(endpointID)));
	} else {
	    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	    exchange.getIn().setBody("Endpoint not found: " + String.valueOf(endpointID));
	    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
	}
    }

    /**
     * Updates the endpoint with the given ID. The parameters of the endpoint
     * are optional, i.e. they can be null. If a parameter is null, the value is
     * not updated.
     * <p>
     * Target method for a camel route. The exchange is created by camel.
     * 
     * @param endpointID
     *            the endpoint id
     * @param exchange
     *            the exchange that contains the received message. Must contain
     *            an instance of {@link Endpoint} in the body. Also serves as
     *            container for the answer.
     * @return A 404-error, if there is no endpoint with the given id. A
     *         422-error, if there was an attempt to update the endpoint url
     *         with an invalid URL.
     */
    public void updateEndpoint(Integer endpointID, Exchange exchange) {
	Endpoint endpoint = exchange.getIn().getBody(Endpoint.class);

	try {
	    if (esa.updateEndpoint(endpointID, endpoint.getEndpointName(),
		    endpoint.getEndpointDescription(), endpoint.getSituations(),
		    endpoint.getOperation(), endpoint.getEndpointURL(),
		    endpoint.getArchiveFilename(), endpoint.getEndpointStatus())) {
		exchange.getIn().setBody(new RestAnswer("Endpoint successfully updated",
			String.valueOf(endpointID)));
	    } else {
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
		exchange.getIn().setBody("Endpoint " + endpointID + " not found.");
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
	    }
	} catch (InvalidEndpointException e) {
	    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	    exchange.getIn().setBody(e.getMessage());
	    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
	}

    }

    /**
     * Adds the situation to the endpoint.
     * <p>
     * Target method for a camel route. The exchange is created by camel.
     * 
     * @param The
     *            endpoint the situation is added to.
     * @param exchange
     *            the exchange that contains the received message. Must contain
     *            an instance of {@link HandledSituation} in the body. Also
     *            serves as container for the answer.
     * @return The id of the new situation. An 422-Error if the situation is
     *         invalid
     */
    public void addHandledSituation(Integer endpointId, Exchange exchange) {
	HandledSituation handledSituation = exchange.getIn().getBody(HandledSituation.class);

	try {
	    int sitId = esa.addHandledSituation(endpointId, handledSituation);

	    exchange.getIn().setBody(
		    new RestAnswer("Situation successfully added.", String.valueOf(sitId)));
	} catch (InvalidEndpointException e) {
	    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	    exchange.getIn().setBody(e.getMessage());
	    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
	}
    }

    /**
     * Gets all situations associated with an endpoint.
     * <p>
     * Target method for a camel route. The exchange is created by camel.
     * 
     * @param endpointId
     *            the id of the endpoint
     * @param exchange
     *            the exchange that contains the received message. Also serves
     *            as container for the answer.
     * @return The situations as list. If there are no situations, an empty list
     *         is returned. A 404-error if no endpoint with this id exists. The
     *         return value is stored in the exchange.
     */
    public void getAllHandledSituations(Integer endpointId, Exchange exchange) {
	List<HandledSituation> situations = esa.getSituationsByEndpoint(endpointId);
	if (situations == null) {
	    exchange.getIn().setBody("Endpoint with id " + endpointId + " not found.");
	    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
	    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	} else {
	    exchange.getIn().setBody(situations);
	}
    }

    /**
     * Gets a specific situation by id.
     * <p>
     * Target method for a camel route. The exchange is created by camel.
     * 
     * @param situationId
     *            the id of the situation.
     * @param exchange
     *            the exchange that contains the received message. Also serves
     *            as container for the answer.
     * @return The situation. A 404-error if no situation with this id exists.
     *         The return value is stored in the exchange.
     */
    public void getHandledSituation(Integer situationId, Exchange exchange) {
	HandledSituation handledSituation = esa.getHandledSituationById(situationId);
	if (handledSituation == null) {
	    exchange.getIn().setBody("Situation with id " + situationId + " not found.");
	    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
	    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	} else {
	    exchange.getIn().setBody(handledSituation);
	}
    }

    /**
     * Deletes the situation with the given id.
     * <p>
     * Target method for a camel route. The exchange is created by camel.
     * 
     * @param situationId
     *            the endpoint id
     * @param exchange
     *            the exchange that contains the received message. Also serves
     *            as container for the answer.
     * @return A 404-error, if there is no situation with the given id.
     */
    public void deleteHandledSituation(Integer situationId, Exchange exchange) {

	if (esa.deleteHandledSituation(situationId)) {
	    exchange.getIn().setBody(
		    new RestAnswer("Situation Successfully deleted", String.valueOf(situationId)));
	} else {
	    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	    exchange.getIn().setBody("Situation not found: " + String.valueOf(situationId));
	    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
	}
    }

    /**
     * Updates the situation with the given ID. The parameters of the situation
     * are optional, i.e. they can be null. If a parameter is null, the value is
     * not updated.
     * <p>
     * Target method for a camel route. The exchange is created by camel.
     * 
     * @param situationId
     *            the situation id
     * @param exchange
     *            the exchange that contains the received message. Must contain
     *            an instance of {@link HandledSituation} in the body. Also
     *            serves as container for the answer.
     * @return A 404-error, if there is no situation with the given id. A
     *         422-error, if the situation is somehow invalid.
     */
    public void updateHandledSituation(Integer situationId, Exchange exchange) {
	HandledSituation handledSituation = exchange.getIn().getBody(HandledSituation.class);

	try {
	    if (esa.updateHandledSituation(situationId, handledSituation)) {
		exchange.getIn().setBody(new RestAnswer("Situation successfully updated",
			String.valueOf(situationId)));
	    } else {
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
		exchange.getIn().setBody("situationId " + situationId + " not found.");
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
	    }
	} catch (InvalidEndpointException e) {
	    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	    exchange.getIn().setBody(e.getMessage());
	    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
	}
    }

}

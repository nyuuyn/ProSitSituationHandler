package api.configuration;

import org.apache.camel.Exchange;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.EndpointStorageAccess;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;

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
	EndpointStorageAccess esa;

	/**
	 * Creates a new instance of EndpointApi and does necessary configuration.
	 */
	public EndpointAPI() {
		esa = StorageAccessFactory.getEndpointStorageAccess();
	}

	/**
	 * Gets all endpoints that are currently stored in the rule directory.
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
	 * @return The id of the new rule. An 422-Error if the endpoint url is not a
	 *         valid URL. The return value is stored in the exchange.
	 */
	public void addEndpoint(Exchange exchange) {
		Endpoint endpoint = exchange.getIn().getBody(Endpoint.class);

		try {

			int endpointID = esa.addEndpoint(endpoint.getOperation(),
					endpoint.getSituation(), endpoint.getEndpointURL());

			exchange.getIn().setBody(
					new RestAnswer(
							"Endpoint successfully added.",
							String.valueOf(endpointID)));
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
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,
					"text/plain");
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
			exchange.getIn().setBody(new RestAnswer("Endpoint Successfully deleted", String.valueOf(endpointID)));
		} else {
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setBody("Endpoint not found: "+String.valueOf(endpointID));
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
			System.out.println(endpoint.getSituation());

			if (esa.updateEndpoint(endpointID, endpoint.getSituation(),
					endpoint.getOperation(), endpoint.getEndpointURL())) {
				exchange.getIn().setBody(new RestAnswer("Endpoint successfully updated", String.valueOf(endpointID)));
			} else {
				exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
				exchange.getIn().setBody(
						"Endpoint " + endpointID + " not found.");
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}
		} catch (InvalidEndpointException e) {
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setBody(e.getMessage());
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
		}

	}

}

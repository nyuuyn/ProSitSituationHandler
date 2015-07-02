package api.configuration;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.camel.Exchange;

import situationHandling.storage.EndpointStorageAccess;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Endpoint;

public class EndpointAPI {

	EndpointStorageAccess esa;

	public EndpointAPI() {
		esa = StorageAccessFactory.getEndpointStorageAccess();
	}

	public void getEndpoints(Exchange exchange) {
		exchange.getIn().setBody(esa.getAllEndpoints());
	}

	public void addEndpoint(Exchange exchange) {
		Endpoint endpoint = exchange.getIn().getBody(Endpoint.class);
		URL endpointURL;
		try {
			endpointURL = new URL(endpoint.getEndpointURL());
			int endpointID = esa.addEndpoint(endpoint.getOperation(),
					endpoint.getSituation(), endpointURL);

			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setBody(
					"Endpoint successfully added. New endpoint id is "
							+ endpointID);
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
		} catch (MalformedURLException e) {
			exchange.getIn().setBody("Invalid endpoint url.");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
		}
	}

	public void getEndpointByID(Integer endpointID, Exchange exchange) {
		Endpoint endpoint = esa.getEndpointByID(endpointID);
		if (endpoint == null) {
			exchange.getIn().setBody("Endpoint " + endpointID + " not found.");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		} else {
			exchange.getIn().setBody(endpoint);
		}
	}

	public void deleteEndpoint(Integer endpointID, Exchange exchange) {

		if (esa.deleteEndpoint(endpointID)) {
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setBody("Endpoint Successfully deleted");
		} else {
			exchange.getIn().setBody("Endpoint " + endpointID + " not found.");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		}
	}

	public void updateEndpoint(Integer endpointID, Exchange exchange) {
		Endpoint endpoint = exchange.getIn().getBody(Endpoint.class);

		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
		URL endpointURL = null;
		try {
			// the URL is optional in this case, so check for null before
			// converting.
			if (endpoint.getEndpointURL() != null) {
				endpointURL = new URL(endpoint.getEndpointURL());
			}
			
			System.out.println(endpoint.getSituation());

			if (esa.updateEndpoint(endpointID, endpoint.getSituation(),
					endpoint.getOperation(), endpointURL)) {
				exchange.getIn().setBody("Endpoint successfully updated");
			} else {
				exchange.getIn().setBody(
						"Endpoint " + endpointID + " not found.");
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}
		} catch (MalformedURLException e) {
			exchange.getIn().setBody("No update possible, due to invalid URL");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
		}

	}

}

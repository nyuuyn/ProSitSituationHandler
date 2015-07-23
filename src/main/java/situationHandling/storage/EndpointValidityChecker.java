package situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import situationHandling.exceptions.InvalidEndpointException;

//TODO: Diese Klasse ist momentan noch sehr umständlich und eigentlich auch überflüssig. Die könnte man genauso gut löschen, falls hier keine weiteren Checks hinzukommen.
class EndpointValidityChecker {

	private String endpointUrl;

	private static final Logger logger = Logger
			.getLogger(EndpointValidityChecker.class);

	EndpointValidityChecker(String endpointURL) {
		this.endpointUrl = endpointURL;
	}

	private void checkUrl() throws InvalidEndpointException {
		try {
			new URL(endpointUrl);
		} catch (MalformedURLException e) {
			logger.error("Invalid endpoint URL " + endpointUrl);
			throw new InvalidEndpointException("Invalid Endpoint URL", e);
		}
	}

	public void checkBeforeAdd() throws InvalidEndpointException {
		checkUrl();
	}

	public void checkBeforeUpdate() throws InvalidEndpointException {
		if (endpointUrl != null) {
			checkUrl();
		}
	}

}

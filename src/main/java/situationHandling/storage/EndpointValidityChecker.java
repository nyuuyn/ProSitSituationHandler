package situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.HandledSituation;

//TODO: Doc
class EndpointValidityChecker {

	private String endpointUrl;
	private List<HandledSituation> situations;

	private static final Logger logger = Logger
			.getLogger(EndpointValidityChecker.class);

	/**
	 * 
	 * @param endpointURL
	 * @param situations can be null (update)
	 */
	EndpointValidityChecker(String endpointURL, List<HandledSituation> situations) {
		this.endpointUrl = endpointURL;
		this.situations = situations;
	}

	private void checkUrl() throws InvalidEndpointException {
		try {
			new URL(endpointUrl);
		} catch (MalformedURLException e) {
			logger.info("Endpoint not valid. Invalid endpoint URL " + endpointUrl);
			throw new InvalidEndpointException("Invalid Endpoint URL", e);
		}
	}
	
	private void checkSituations() throws InvalidEndpointException{
		if (situations == null || situations.size() < 1){
			logger.info("Endpoint not valid. An endpoint must contain at least one situation. ");
			throw new InvalidEndpointException("An endpoint must contain at least one situation.");
		}
	}

	public void checkBeforeAdd() throws InvalidEndpointException {
		checkUrl();
		checkSituations();
	}

	public void checkBeforeUpdate() throws InvalidEndpointException {
		if (endpointUrl != null) {
			checkUrl();
		}
	}

}

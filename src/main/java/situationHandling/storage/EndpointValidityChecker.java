package situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;

/**
 * The Class Endpoint ValidityChecker is used to do semantic checks on an
 * endpoint. <br>
 * 
 * Semantic checks refer to the URL and the situations of an endpoint. It is
 * checked if:
 * <ol>
 * <li>The URL is valid</li>
 * <li>There is at least one handled situation specified.</li>
 * </ol>
 * 
 * Uses the {@link InvalidEndpointException} to signalize constraint violations.
 * 
 * @see Endpoint
 * @see HandledSituation
 */
class EndpointValidityChecker {

	/**
	 * The endpoint url.
	 */
	private String endpointUrl;
	/**
	 * Situations handled
	 */
	private List<HandledSituation> situations;

	/**
	 * logger
	 */
	private static final Logger logger = Logger
			.getLogger(EndpointValidityChecker.class);

	/**
	 * 
	 * Creates a new instance of {@link EndpointValidityChecker}.
	 * 
	 * @param endpointURL
	 *            the endpoint url
	 * @param situations
	 *            the situations to check. Can be null, if the check is done
	 *            before an update.
	 */
	EndpointValidityChecker(String endpointURL,
			List<HandledSituation> situations) {
		this.endpointUrl = endpointURL;
		this.situations = situations;
	}

	/**
	 * Check url validity
	 * 
	 * @throws InvalidEndpointException
	 *             When URL is invalid
	 */
	private void checkUrl() throws InvalidEndpointException {
		try {
			new URL(endpointUrl);
		} catch (MalformedURLException e) {
			logger.info("Endpoint not valid. Invalid endpoint URL "
					+ endpointUrl);
			throw new InvalidEndpointException("Invalid Endpoint URL", e);
		}
	}

	/**
	 * Check situation validity.
	 * 
	 * @throws InvalidEndpointException
	 *             when <1 situations are specified.
	 */
	private void checkSituations() throws InvalidEndpointException {
		if (situations == null || situations.size() < 1) {
			logger.info("Endpoint not valid. An endpoint must contain at least one situation. ");
			throw new InvalidEndpointException(
					"An endpoint must contain at least one situation.");
		}
	}

	/**
	 * 
	 * Does the described checks {@link EndpointValidityChecker}. Intended to
	 * use before adding a new endpoint.
	 * 
	 * @throws InvalidEndpointException
	 *             when the endpoint is invalid for the described reasons.
	 */
	public void checkBeforeAdd() throws InvalidEndpointException {
		checkUrl();
		checkSituations();
	}

	/**
	 * 
	 * Does the described checks {@link EndpointValidityChecker}. Intended to
	 * use before an update.
	 * 
	 * @throws InvalidEndpointException
	 *             when the endpoint is invalid for the described reasons.
	 */
	public void checkBeforeUpdate() throws InvalidEndpointException {
		if (endpointUrl != null) {
			checkUrl();
		}
	}

}

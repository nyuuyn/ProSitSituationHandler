package situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Endpoint.EndpointStatus;
import situationHandling.storage.datatypes.HandledSituation;

/**
 * The Class Endpoint ValidityChecker is used to do semantic checks on an
 * endpoint. <br>
 * 
 * Semantic checks refer to the status, url and archive name. It is checked if:
 * <ol>
 * <li>the right values are set</li>
 * <li>The URL is valid</li>
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
     * The name of the archive file
     */
    private String archiveFilename;

    /**
     * The status of the endpoint.
     */
    private EndpointStatus endpointStatus;

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(EndpointValidityChecker.class);

    /**
     * 
     * Creates a new instance of {@link EndpointValidityChecker}.
     * 
     * @param endpointURL
     *            the endpoint url
     * @param archiveFilename
     *            the archive
     * @param endpointStatus
     *            the status
     */
    EndpointValidityChecker(String endpointURL, String archiveFilename,
	    EndpointStatus endpointStatus) {
	this.endpointUrl = endpointURL;
	this.archiveFilename = archiveFilename;
	this.endpointStatus = endpointStatus;
    }

    /**
     * Checks the endpoint status and filename/url
     * 
     * @throws InvalidEndpointException
     *             When status/url/filename is invalid
     */
    private void checkStatus() throws InvalidEndpointException {
	if (endpointStatus == null) {
	    throw new InvalidEndpointException("No endpoint status set");
	} else if (endpointStatus == EndpointStatus.archive && archiveFilename == null) {
	    throw new InvalidEndpointException("Status archive but no filename");
	} else if (endpointStatus == EndpointStatus.available && endpointUrl != null) {
	    checkUrl();
	} else if (endpointStatus == EndpointStatus.available && endpointUrl == null) {
	    throw new InvalidEndpointException("Status available, but no url specified");
	}
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
	    logger.info("Endpoint not valid. Invalid endpoint URL " + endpointUrl);
	    throw new InvalidEndpointException("Invalid Endpoint URL", e);
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
	checkStatus();
	// checkSituations();
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

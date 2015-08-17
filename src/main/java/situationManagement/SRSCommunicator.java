package situationManagement;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import main.CamelUtil;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.http.HttpOperationFailedException;
import org.apache.log4j.Logger;

import situationHandling.storage.datatypes.Situation;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class SRSCommunicator does the communication with the situation
 * recognition system. It can be used to send requests and get the answers by
 * the SRS.
 */
class SRSCommunicator {

	/** The logger for this class. */
	private final static Logger logger = Logger.getLogger(SRSCommunicator.class);

	/** The srs url. */
	private URL srsUrl;

	/**
	 * Instantiates a new SRS communicator.
	 *
	 * @param srsUrl
	 *            the url of the srs. All requests will be sent to this url.
	 */
	SRSCommunicator(URL srsUrl) {
		this.srsUrl = srsUrl;
	}

	/**
	 * Subscribe to a situation.
	 *
	 * @param situation
	 *            the situation to subscribe to
	 * @param address
	 *            the address to which the SRS should send the situation changes
	 *            (should be the URL of the situation handler).
	 * @return true, if subscription was successful
	 */
	boolean subscribe(Situation situation, URL address) {
		ProducerTemplate pt = CamelUtil.getProducerTemplate();

		// query string
		String query = "SitTempID=" + situation.getSituationName() + "&ThingID=" + situation.getObjectName()
				+ "&CallbackURL=" + address + "&once=false";

		// set headers
		Map<String, Object> headers = new HashMap<>();
		headers.put(Exchange.HTTP_METHOD, "POST");
		headers.put(Exchange.HTTP_QUERY, query);
		headers.put(Exchange.HTTP_PATH, "/situations/changes"); // path
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		headers.put("Accept-Encoding", "gzip, deflate");

		// send request and handle errors
		try {
			pt.requestBodyAndHeaders(srsUrl.toString(), "", headers, String.class);
			logger.debug("Successfully registrated on " + situation);
			return true;
		} catch (CamelExecutionException e) {
			if (e.getCause() instanceof HttpOperationFailedException) {
				HttpOperationFailedException httpOperationFailedException = (HttpOperationFailedException) e.getCause();
				if (httpOperationFailedException.getStatusCode() == 400
						&& httpOperationFailedException.getResponseBody().equals("\"Already registrated\"")) {
					logger.debug("Already registered on: " + situation);
					return true;
				} else if (httpOperationFailedException.getStatusCode() == 404) {
					logger.info("Could not register on: " + situation + ". Situation does not exist!.");
				} else {
					logger.error("Error when registering on " + situation, e);
				}
			} else {
				logger.error("Error when registering on " + situation, e);
			}

		}
		return false;
	}

	/**
	 * Unsubscribe from a situation.
	 *
	 * @param situation
	 *            the situation to unsubscribe from
	 * @param address
	 *            the address the situation changes were sent too. Must be the
	 *            same that was used for subscription, see
	 *            {@code SRSCommunicator#subscribe(Situation, URL)}
	 * @return true, if unsubscription was successful
	 */
	boolean unsubscribe(Situation situation, URL address) {
		ProducerTemplate pt = CamelUtil.getProducerTemplate();

		// query string
		String query = "SitTempID=" + situation.getSituationName() + "&ThingID=" + situation.getObjectName()
				+ "&CallbackURL=" + address;

		// set headers
		Map<String, Object> headers = new HashMap<>();
		headers.put(Exchange.HTTP_METHOD, "DELETE");
		headers.put(Exchange.HTTP_QUERY, query);
		headers.put(Exchange.HTTP_PATH, "/situations/changes"); // path
		headers.put("Accept", "application/json");
		headers.put("Accept-Encoding", "gzip, deflate");

		// send request and handle errors
		try {
			pt.requestBodyAndHeaders(srsUrl.toString(), "", headers, String.class);
			logger.debug("Successfully unsubscribed from " + situation);
			return true;
		} catch (CamelExecutionException e) {
			if (e.getCause() instanceof HttpOperationFailedException) {
				HttpOperationFailedException httpOperationFailedException = (HttpOperationFailedException) e.getCause();
				if (httpOperationFailedException.getStatusCode() == 404) {
					logger.debug("Unsubscribe failed. No registration found for: " + situation);
				} else {
					logger.error("Error when deleting registration on " + situation, e);
				}
			} else {
				logger.error("Error when deleting registration on " + situation, e);
			}
		}
		return false;
	}

	/**
	 * Query the state of a situation from the SRS.
	 *
	 * @param situation
	 *            the situation to query
	 * @return the result delivered by the SRS
	 */
	SituationResult getSituation(Situation situation) {
		ProducerTemplate pt = CamelUtil.getProducerTemplate();

		// query
		String query = "thing=" + situation.getObjectName() + "&situationtemplate=" + situation.getSituationName();

		// set headers
		Map<String, Object> headers = new HashMap<>();
		headers.put(Exchange.HTTP_METHOD, "GET");
		headers.put(Exchange.HTTP_QUERY, query);
		headers.put(Exchange.HTTP_PATH, "/situations/ByThingAndTemplate");

		// send request and handle results
		SituationResult situationResult = null;
		try {
			String answer = pt.requestBodyAndHeaders(srsUrl.toString(), null, headers, String.class);

			situationResult = new ObjectMapper().readValue(answer, SituationResult.class);
			logger.debug("Situation Result: " + situationResult.toString());

		} catch (IOException e) {
			logger.debug("Situation " + situation + " does not exist");
		} catch (CamelExecutionException e) {
			// handle error that situation was not found by srs
			if (e.getCause() instanceof HttpOperationFailedException
					&& ((HttpOperationFailedException) e.getCause()).getStatusCode() == 404) {
				logger.debug("Situation: " + situation + " could not be found. Status could not be fetched.");

			} else {
				logger.error("Error when getting situation state: " + situation, e);
			}

		}

		return situationResult;

	}
}

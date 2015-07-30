package situationManagement;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.http.HttpOperationFailedException;
import org.apache.log4j.Logger;

import routes.CamelUtil;
import situationHandling.storage.datatypes.Situation;

import com.fasterxml.jackson.databind.ObjectMapper;

class SRSCommunicator {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(SRSCommunicator.class);

	private URL srsUrl;

	SRSCommunicator(URL srsUrl) {
		this.srsUrl = srsUrl;
	}

	void subscribe(Situation situation, URL address) {
		ProducerTemplate pt = CamelUtil.getProducerTemplate();

		String query = "SitTempID=" + situation.getSituationName()
				+ "&ThingID=" + situation.getObjectName() + "&CallbackURL="
				+ address + "&once=false";

		Map<String, Object> headers = new HashMap<>();

		headers.put(Exchange.HTTP_METHOD, "POST");
		headers.put(Exchange.HTTP_QUERY, query);
		headers.put(Exchange.HTTP_PATH, "/situations/changes");
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		headers.put("Accept-Encoding", "gzip, deflate");

		try {
			pt.requestBodyAndHeaders(srsUrl.toString(), "",
					headers, String.class);
			logger.debug("Successfully registrated on " + situation);
		} catch (CamelExecutionException e) {
			if (e.getCause() instanceof HttpOperationFailedException) {
				HttpOperationFailedException httpOperationFailedException = (HttpOperationFailedException) e
						.getCause();
				if (httpOperationFailedException.getStatusCode() == 400
						&& httpOperationFailedException.getResponseBody()
								.equals("\"Already registrated\"")) {
					logger.debug("Already registered on: " + situation);
				} else {
					logger.error("Error when registering on " + situation, e);
				}
			} else {
				logger.error("Error when registering on " + situation, e);
			}

		}
	}

	void unsubscribe(Situation situation, URL address) {
		ProducerTemplate pt = CamelUtil.getProducerTemplate();

		String query = "SitTempID=" + situation.getSituationName()
				+ "&ThingID=" + situation.getObjectName() + "&CallbackURL="
				+ address;

		Map<String, Object> headers = new HashMap<>();

		headers.put(Exchange.HTTP_METHOD, "DELETE");
		headers.put(Exchange.HTTP_QUERY, query);
		headers.put(Exchange.HTTP_PATH, "/situations/changes");
		headers.put("Accept", "application/json");
		headers.put("Accept-Encoding", "gzip, deflate");

		try {
			pt.requestBodyAndHeaders(srsUrl.toString(), "",
					headers, String.class);
			logger.debug("Successfully unsubscribed from " + situation);
		} catch (CamelExecutionException e) {
			if (e.getCause() instanceof HttpOperationFailedException) {
				HttpOperationFailedException httpOperationFailedException = (HttpOperationFailedException) e
						.getCause();
				if (httpOperationFailedException.getStatusCode() == 404) {
					logger.debug("Unsubscribe failed. No registration found for: " + situation);
				} else {
					logger.error("Error when deleting registration on " + situation, e);
				}
			} else {
				logger.error("Error when deleting registration on " + situation, e);
			}

		}
	}

	SituationResult getSituation(Situation situation) {
		ProducerTemplate pt = CamelUtil.getProducerTemplate();

		String query = "thing=" + situation.getObjectName()
				+ "&situationtemplate=" + situation.getSituationName();

		Map<String, Object> headers = new HashMap<>();

		headers.put(Exchange.HTTP_METHOD, "GET");
		headers.put(Exchange.HTTP_QUERY, query);
		headers.put(Exchange.HTTP_PATH, "/situations/ByThingAndTemplate");

		String answer = pt.requestBodyAndHeaders(srsUrl.toString(), null,
				headers, String.class);
		SituationResult situationResult = null;
		try {
			situationResult = new ObjectMapper().readValue(answer,
					SituationResult.class);
			logger.debug("Situation Result: " + situationResult.toString());

		} catch (IOException e) {
			logger.debug("Situation " + situation + " does not exist");
		}

		return situationResult;

	}
}

package situationManagement;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
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
		// http://192.168.209.200:10010/situations/changes?ID=test&CallbackURL=test&once=false
		// TODO: Momentan ist das noch quatsch --> die richtige Methode
		// existiert noch nicht
		ProducerTemplate pt = CamelUtil.getProducerTemplate();

		String query = "situationTemplate=" + situation.getSituationName()
				+ "thing=" + situation.getObjectName() + "&CallbackURL="
				+ situation.getSituationName() + "&once=false";

		Map<String, Object> headers = new HashMap<>();

		headers.put(Exchange.HTTP_METHOD, "POST");
		headers.put(Exchange.HTTP_QUERY, query);
		headers.put(Exchange.HTTP_PATH, "/changes");

		// String answer = pt.requestBodyAndHeaders(srsUrl.toString(), null,
		// headers, String.class);
		// SituationResult situationResult = null;
		// try {
		// situationResult = new ObjectMapper().readValue(answer,
		// SituationResult.class);
		// logger.debug("Situation Result: " + situationResult.toString());
		//
		// } catch (IOException e) {
		// logger.debug("Situation " + situation + " does not exist");
		// }
	}

	void unsubscribe(Situation situation, URL address) {
		// curl -X DELETE --header "Accept: application/json"
		// "http://192.168.209.200:10010/situations/changes?ID=test&CallbackURL=test"

		// TODO: Momentan ist das noch quatsch --> die richtige Methode
		// existiert noch nicht
		ProducerTemplate pt = CamelUtil.getProducerTemplate();

		String query = "situationTemplate=" + situation.getSituationName()
				+ "thing=" + situation.getObjectName() + "&CallbackURL="
				+ situation.getSituationName();

		Map<String, Object> headers = new HashMap<>();

		headers.put(Exchange.HTTP_METHOD, "DELETE");
		headers.put(Exchange.HTTP_QUERY, query);
		headers.put(Exchange.HTTP_PATH, "/changes");

		// String answer = pt.requestBodyAndHeaders(srsUrl.toString(), null,
		// headers, String.class);
		// SituationResult situationResult = null;
		// try {
		// situationResult = new ObjectMapper().readValue(answer,
		// SituationResult.class);
		// logger.debug("Situation Result: " + situationResult.toString());
		//
		// } catch (IOException e) {
		// logger.debug("Situation " + situation + " does not exist");
		// }
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
		//TODO: Update Cache!
		return situationResult;

	}
}

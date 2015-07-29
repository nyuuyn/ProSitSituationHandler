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

	private URL srsUrl;

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(SRSCommunicator.class);

	SRSCommunicator(URL srsUrl) {
		this.srsUrl = srsUrl;
	}

	void subscribe() {
		// TODO: Subscribe methode --> benötigt als Param noch die Situation
		// oder so
	}

	void unsubscribe() {
		// TODO:unsubscrobe --> Sitaution als Param
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

	static void receiveSituationChange(Situation situation) {
		// TODO: Das hier als Zielmethode für die entsprechende Camel ROute/Rest
		// OP --> Sit Handling einleiten
		// TODO: fraglich ob das hier überhaupt nötig ist, oder ob die Sit
		// Handler komponente direkt als Ziel dient
		// --> erstmal lassen und eine Klasse noch dazwischen schalten --> die
		// kann dann notfalls noch in den Cache schreiben
	}
}

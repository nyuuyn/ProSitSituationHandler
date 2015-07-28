package situationManagement;

import java.net.URL;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

import routes.CamelUtil;
import situationHandling.storage.datatypes.Situation;

class SRSCommunicator {

	private URL srsUrl;

	// private final String situationPath = "/situations";

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

		String query = "?thing=" + situation.getObjectName()
				+ "situationtemplate=" + situation.getSituationName();

		Exchange exchange = pt.send(srsUrl.toString()
				+ "/situations/ByThingAndTemplate" + query, new Processor() {
			public void process(Exchange exchange) throws Exception {
				exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
			}
		});

		System.out.println(exchange.getOut().getBody(String.class));

		// TODO: Get Abfrage an SRS ; Situation als Param benötigt
		return new SituationResult();
	}

	static void receiveSituationChange(SituationResult situationResult) {
		// TODO: Das hier als Zielmethode für die entsprechende Camel ROute/Rest
		// OP --> Sit Handling einleiten
		// TODO: fraglich ob das hier überhaupt nötig ist, oder ob die Sit
		// Handler komponente direkt als Ziel dient
		// --> erstmal lassen und eine Klasse noch dazwischen schalten --> die
		// kann dann notfalls noch in den Cache schreiben
	}
}

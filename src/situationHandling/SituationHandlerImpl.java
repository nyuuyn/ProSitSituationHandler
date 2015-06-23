package situationHandling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultProducerTemplate;

import situationHandling.storage.RuleStorage;
import situationManagement.Situation;

//TODO: Irgendwie ist das hier relativ scheisse --> durch das Public wird das Vorgehen mit der Factory usw aufgebrochen. Allerdings ist das noetig, um Situationen mit Camel zu handhaben. Alternativ koennte man hier auch zwei Klassen erstellen und das Pattern nur fuer eine aufbrechen. Zudem koennte man das geanze statische Zeug aufbrechen und die Objekte mit Spring injecten

public class SituationHandlerImpl extends RouteBuilder implements
		SituationHandler {

	private static RuleStorage ruleStorage = new RuleStorage();;

	private static ExecutorService threadExecutor = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public SituationHandlerImpl() {

	}

	public RuleStorage getRuleStorage() {
		return ruleStorage;
	}

	@Override
	public void situationOccured(Situation situation) {

		// TODO: Situation mithilfe der Regelbasis abhandeln
		// for (DummyAction da : ruleStorage.getActionsForSituation(situation,
		// situationObject)) {
		//
		// threadExecutor.submit(new ActionExecutor(da));
		//
		// }
		

	}

	@Handler
	public void receivedOperationCall(Exchange exchange) {
		CamelContext camelContext = getContext();

		ProducerTemplate pt = new DefaultProducerTemplate(camelContext);
		try {
			pt.start();

			String body = exchange.getIn().getBody(String.class);

			// xpath should be: name(/soapenv:envelope/soapenv:body/*)
			// leider kein valides XML im Body dann gehts nicht

			// String situation = new SituationManager().getSituation();
			// String operation = getOperationName(body);
			//
			// String ret = pt.requestBody(
			// new EndpointDirectory().getEndpoint(operation, situation),
			// body, String.class);
			// exchange.getIn().setBody(ret, String.class);

			String ret = pt.requestBody("http://localhost:4434/miniwebservice",
					body, String.class);
			exchange.getIn().setBody(ret, String.class);

			pt.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void configure() throws Exception {
		// not needed

	}

}

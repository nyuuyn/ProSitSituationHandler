package situationHandling;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.PluginParams;
import situationHandling.storage.EndpointStorageAccess;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

//TODO: Irgendwie ist das hier relativ scheisse --> durch das Public wird das Vorgehen mit der Factory usw aufgebrochen. Allerdings ist das noetig, um Situationen mit Camel zu handhaben. Alternativ koennte man hier auch zwei Klassen erstellen und das Pattern nur fuer eine aufbrechen. Zudem koennte man das geanze statische Zeug aufbrechen und die Objekte mit Spring injecten

public class OperationHandlerImpl implements OperationHandler {

	private static ExecutorService threadExecutor = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public OperationHandlerImpl() {

	}

	@Handler
	public void receivedOperationCall(Exchange exchange) {

		try {
			String soapBody = exchange.getIn().getBody(String.class);

			// xpath should be: name(/soapenv:envelope/soapenv:body/*)
			// leider kein valides XML im Body dann gehts nicht

			PluginManager pm = PluginManagerFactory.getPluginManager();
			PluginParams params = new PluginParams();

			params.setParam("Http method", "POST");
			// TODO: Eigentlich ist es scheiße, dass die Plugins eigene Producer
			// Templates verwenden!
			// TODO: Momentan ist das doch noch relativ synchron (egal, falls
			// Camel jedes mal ein neues Objekt erstellt

			EndpointStorageAccess esa = StorageAccessFactory
					.getEndpointStorageAccess();

			String operationName = SoapParser.getOperationName(soapBody);
			String qualifier = exchange.getIn()
					.getHeader("CamelHttpPath", String.class).replace("/", "")
					.trim();

			// Woher krieg ich eigentlich das Objekt, wenn eine Operation
			// aufgerufen wird? Z.B. Ich krieg Operation x rein --> jetzt schau
			// ich im directory nachWoher krieg ich eigentlich das Objekt, wenn
			// eine Operation aufgerufen wird? Z.B. Ich krieg Operation x rein
			// -->
			// jetzt schau ich im directory nachWoher krieg ich eigentlich das
			// Objekt, wenn eine Operation aufgerufen wird? Z.B. Ich krieg
			// Operation x rein --> jetzt schau ich nach, welche Situationen es
			// gerade gibt und lass ne Query gegen die DB laufen, in der alle
			// möglichen Kombinationen abgefragt werden..Sinnvolles Vorgehen?
			// was tun bei mehreren möglichen Endpunkten?

//			String url = esa.getEndpointURL(
//					new Situation("situation1", "object1"),
//					new Operation(operationName, qualifier)).toString();
//
//			Future<Map<String, String>> response = threadExecutor.submit(pm
//					.getPluginSender("situationHandler.http", url, soapBody,
//							params));
//
//			exchange.getIn().setBody(response.get().get("body"), String.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

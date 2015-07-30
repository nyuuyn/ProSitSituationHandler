package situationManagement;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import situationHandling.OperationHandlerFactory;
import situationHandling.SituationHandlerFactory;
import situationHandling.storage.datatypes.Situation;

public class SituationEndpoint {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(SituationEndpoint.class);

	public void situationReceived(Exchange exchange) {
//		 String changedSituation = exchange.getIn().getBody(String.class);
//		 System.out.println(changedSituation);

		// logger.debug("Received notification about situation change:\n " +
		// changedSituation);

		 //parse CONTENT from headers...wtf srs api
		String situationTemplate = exchange.getIn().getHeader(
				"doc[situationtemplate]", String.class);
		String thing = exchange.getIn().getHeader("doc[thing]", String.class);	
		boolean occured = Boolean.parseBoolean(exchange.getIn().getHeader(
				"doc[occured]", String.class));

		logger.debug("Received notification about situation change: Template: "
				+ situationTemplate + " | thing: " + thing + " | occured: "
				+ occured);

		SituationResult situationResult;

		// try {
		// transform situation
		/*
		 * for JSON --> does not work at the moment situationResult = new
		 * ObjectMapper().readValue(changedSituation, SituationResult.class);
		 * Situation situation = new Situation(
		 * situationResult.getSituationtemplate(), situationResult.getThing());
		 */

		Situation situation = new Situation(situationTemplate, thing);
		situationResult = new SituationResult();
		situationResult.setOccured(occured);

		// notify notification and workflow handling
		SituationHandlerFactory.getSituationHandler().situationChanged(
				situation, situationResult.isOccured());
		OperationHandlerFactory.getOperationHandler().situationChanged(
				situation, situationResult.isOccured());

		// updates the cache, if the cache is enabled
		SituationManager situationManager = SituationManagerFactory
				.getSituationManager();
		if (situationManager instanceof SituationManagerWithCache) {
			SituationManagerWithCache cache = (SituationManagerWithCache) situationManager;
			cache.updateSituationCache(
					new Situation(situation.getSituationName(), situation
							.getObjectName()), situationResult.isOccured());
		}

		// } catch (IOException e) {
		// logger.warn("Received invalid message from Situation Recognition System."
		// + e.getMessage());
		// }

	}

}

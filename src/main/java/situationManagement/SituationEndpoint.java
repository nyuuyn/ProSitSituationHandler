package situationManagement;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import situationHandling.OperationHandlerFactory;
import situationHandling.SituationHandlerFactory;
import situationHandling.storage.datatypes.Situation;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SituationEndpoint {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(SituationEndpoint.class);

	public void situationReceived(Exchange exchange) {
		String changedSituation = exchange.getIn().getBody(String.class);

		SituationResult situationResult;
		try {
			// transform situation
			situationResult = new ObjectMapper().readValue(changedSituation,
					SituationResult.class);
			Situation situation = new Situation(
					situationResult.getSituationtemplate(),
					situationResult.getThing());
			
			// notify notification and workflow handling
			SituationHandlerFactory.getSituationHandler().situationChanged(
					situation, situationResult.isOccured());
			OperationHandlerFactory.getOperationHandler().situationChanged(
					situation, situationResult.isOccured());
			
			//updates the cache, if the cache is enabled
			SituationManager situationManager = SituationManagerFactory.getSituationManager();
			if (situationManager instanceof SituationManagerWithCache){
				SituationManagerWithCache cache = (SituationManagerWithCache) situationManager;
				cache.updateSituationCache(situationResult.getSituation(), situationResult.isOccured());
			}

		} catch (IOException e) {
			logger.warn("Received invalid message from Situation Recognition System."
					+ e.getMessage());
		}

	}

}

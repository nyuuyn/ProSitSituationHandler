package situationManagement;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import situationHandling.notifications.NotificationComponentFactory;
import situationHandling.storage.datatypes.Situation;
import situationHandling.workflowOperations.OperationHandlerFactory;

/**
 * The Class SituationEndpoint implements the endpoint that is used to receive
 * Situation changes from the SRS. It processes the message from the SRS and
 * forwards the situation change to the situation handling. Furthermore, it
 * updates the situation cache, if enabled.
 * <p>
 * <h1>Note:</h1> This class is only intended to be used as the target for a
 * camel route and NOT for manual usage.
 * 
 */
public class SituationEndpoint {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(SituationEndpoint.class);

	/**
	 * The situation received method is to be used by camel when a message
	 * arrives at the "situation change" endpoint. It does the processing of the
	 * received message.
	 *
	 * @param exchange
	 *            the exchange that contains the message. Populated by camel
	 */
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

			logger.debug("Received notification about situation change: Template: "
					+ situation.getSituationName()
					+ " | thing: "
					+ situation.getObjectName()
					+ " | occured: "
					+ situationResult.isOccured());

			// updates the cache, if the cache is enabled
			SituationManager situationManager = SituationManagerFactory
					.getSituationManager();
			if (situationManager instanceof SituationManagerWithCache) {
				SituationManagerWithCache cache = (SituationManagerWithCache) situationManager;
				cache.updateSituationCache(
						new Situation(situation.getSituationName(), situation
								.getObjectName()), situationResult.isOccured());
			}

			// notify notification and workflow handling
			NotificationComponentFactory.getNotificationComponent().situationChanged(
					situation, situationResult.isOccured());
			OperationHandlerFactory.getOperationHandler().situationChanged(
					situation, situationResult.isOccured());

		} catch (IOException e) {
			logger.warn("Received invalid message from Situation Recognition System."
					+ e.getMessage());
		}

	}

}

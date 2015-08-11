package situationHandling;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.PluginParams;
import situationHandling.storage.RuleStorageAccess;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Action.ExecutionTime;
import situationHandling.storage.datatypes.Situation;

class SituationHandlerImpl implements SituationHandler {

	private static Logger logger = Logger.getLogger(SituationHandlerImpl.class);

	private static ExecutorService threadExecutor = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	@Override
	public void situationChanged(Situation situation, boolean state) {

		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();
		PluginManager pm = PluginManagerFactory.getPluginManager();

		ExecutionTime time = state ? ExecutionTime.onSituationAppear
				: ExecutionTime.onSituationDisappear;
		List<Action> actions = rsa.getActionsBySituationAndExecutionTime(
				situation, time);
		logger.debug("Executing actions:\n" + actions.toString());

		// TODO: Das vllt anders machen (keine doppelte schleife usw. -->
		// ausserdem scheisse weil das ergebnis nicht berücksichtigt wird
		for (Action action : actions) {
			StorageAccessFactory.getHistoryAccess().appendAction(action,
					situation, state);
		}

		actions.forEach(action -> threadExecutor.submit(pm.getPluginSender(
				action.getPluginID(), action.getAddress(), action.getPayload(),
				new PluginParams(action.getParams()))));

	}

}

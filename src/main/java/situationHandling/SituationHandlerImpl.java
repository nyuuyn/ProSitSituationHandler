package situationHandling;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.PluginParams;
import situationHandling.storage.RuleStorageAccess;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Situation;

class SituationHandlerImpl implements SituationHandler {

	private static ExecutorService threadExecutor = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	@Override
	public void handleSituation(Situation situation) {

		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();
		PluginManager pm = PluginManagerFactory.getPluginManager();

		List<Action> actions = rsa.getActionsBySituation(situation);
		//TODO: Hier muss noch auf true und false überprüft werden!
		actions.forEach(action -> threadExecutor.submit(pm.getPluginSender(
				action.getPluginID(), action.getAddress(), action.getPayload(),
				new PluginParams(action.getParams()))));

	}

}

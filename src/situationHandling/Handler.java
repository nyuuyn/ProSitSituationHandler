package situationHandling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import situationHandling.storage.DummyAction;
import situationHandling.storage.RuleStorage;

public class Handler {

	private static RuleStorage ruleStorage;

	private ExecutorService threadExecutor;

	public Handler() {
		// create only one rule storage to be used by all handlers (TODO: eher
		// als Singleton?)
		if (ruleStorage == null) {
			ruleStorage = new RuleStorage();
		}

		int threadAmount = Runtime.getRuntime().availableProcessors();
		threadExecutor = Executors.newFixedThreadPool(threadAmount);

	}

	public RuleStorage getRuleStorage() {
		return ruleStorage;
	}

	public void SituationOccured(String situation, String situationObject) {

		// TODO: Situation mithilfe der Regelbasis abhandeln
		for (DummyAction da : ruleStorage.getActionsForSituation(situation,
				situationObject)) {

			threadExecutor.submit(new ActionExecutor(da));
			
		}

	}

	public void receivedOperationCall() {

		// TODO: operation durch Workflow aufgerufen

	}

}

package situationHandling.notifications;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.PluginParams;
import situationHandling.storage.RuleStorageAccess;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Action.ExecutionTime;
import situationHandling.storage.datatypes.Situation;

/**
 * The Class NotificationComponentImpl implements the funtionality of the
 * {@code NotificationComponent} Interface. It executes the actions in an
 * asynchronous way and uses several threads to execute the action. This means
 * that there is no exact guarantee WHEN the actions will be executed and in
 * which order. However, execution will happen as soon as possible.
 */
class NotificationComponentImpl implements NotificationComponent {

    /** The logger. */
    private static final Logger logger = Logger.getLogger(NotificationComponentImpl.class);

    /** The thread executor. */
    private static ExecutorService threadExecutor = Executors
	    .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /*
     * (non-Javadoc)
     * 
     * @see
     * situationHandling.notifications.NotificationComponent#situationChanged(
     * situationHandling.storage.datatypes.Situation, boolean)
     */
    @Override
    public void situationChanged(Situation situation, boolean state) {

	RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();
	PluginManager pm = PluginManagerFactory.getPluginManager();

	ExecutionTime time = state ? ExecutionTime.onSituationAppear
		: ExecutionTime.onSituationDisappear;
	List<Action> actions = rsa.getActionsBySituationAndExecutionTime(situation, time);
	logger.debug("Executing actions:\n" + actions.toString());

	LinkedList<ActionResultWrapper> results = new LinkedList<>();
	for (Action action : actions) {

	    Callable<Map<String, String>> plugin = pm.getPluginSender(action.getPluginID(),
		    action.getAddress(), action.getPayload(), new PluginParams(action.getParams()));

	    if (plugin != null) {
		Future<Map<String, String>> result = threadExecutor.submit(plugin);
		results.add(new ActionResultWrapper(action, result));
	    } else {
		logger.warn("Could not find plugin: " + action.getPluginID() + ". Action "
			+ action.getId() + " was not executed.");
	    }

	}

	// extra threads waits for the actions to execute
	threadExecutor.execute(new Runnable() {
	    @Override
	    public void run() {
		for (ActionResultWrapper arw : results) {
		    try {
			StorageAccessFactory.getHistoryAccess().appendAction(arw.getAction(),
				situation, state, arw.getResult().get());
			logger.debug(
				"Action executed - Result: " + arw.getResult().get().toString());
		    } catch (InterruptedException | ExecutionException e) {
			logger.error("Failure when executing action.", e);
		    }
		}
	    }
	});

    }

    @Override
    public void shutdown() {
	threadExecutor.shutdown();
	
    }

}

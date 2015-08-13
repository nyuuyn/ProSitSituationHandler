package situationHandling.notifications;

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

	// TODO: Das vllt anders machen (keine doppelte schleife usw. -->
	// ausserdem scheisse weil das ergebnis nicht berücksichtigt wird
	for (Action action : actions) {
	    StorageAccessFactory.getHistoryAccess().appendAction(action, situation, state);
	}

	actions.forEach(action -> threadExecutor.submit(pm.getPluginSender(action.getPluginID(),
		action.getAddress(), action.getPayload(), new PluginParams(action.getParams()))));

    }

}

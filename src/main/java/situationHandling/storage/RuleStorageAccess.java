package situationHandling.storage;

import java.util.HashMap;
import java.util.List;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Situation;

public interface RuleStorageAccess {

	public int addAction(Situation situation, Action action);

	public boolean removeAction(Situation situation, int id);

	public boolean updateAction(int actionID, String pluginID, String address,
			String message, HashMap<String, String> params);

	public List<Action> getActionsBySituation(Situation situation);

	public List<Action> getAllActions();

}

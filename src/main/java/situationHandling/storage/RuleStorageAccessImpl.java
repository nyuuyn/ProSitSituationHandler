package situationHandling.storage;

import java.util.HashMap;
import java.util.List;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Situation;

public class RuleStorageAccessImpl implements RuleStorageAccess {

	@Override
	public int addAction(Situation situation, Action action) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean removeAction(Situation situation, int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateAction(int actionID, String pluginID, String address,
			String message, HashMap<String, String> params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Action> getActionsBySituation(Situation situation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Action> getAllActions() {
		// TODO Auto-generated method stub
		return null;
	}

}

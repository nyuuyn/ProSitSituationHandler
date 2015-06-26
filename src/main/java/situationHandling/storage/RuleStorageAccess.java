package situationHandling.storage;

import java.util.HashMap;
import java.util.List;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

public interface RuleStorageAccess {

	public int addRule(Situation situation, List<Action> actions);

	public int addAction(int ruleID, Action action);

	public boolean removeAction(int ruleID, int ActionID);

	public boolean removeRule(int ruleID);

	public boolean updateAction(int actionID, String pluginID, String address,
			String message, HashMap<String, String> params);

	public boolean updateRule(int ruleID, String situationName,
			String objectName, List<Action> actions);

	public List<Rule> getAllRules();

	public List<Action> getActionsBySituation(Situation situation);

	public List<Action> getActionsByRuleID(int ruleID);

}

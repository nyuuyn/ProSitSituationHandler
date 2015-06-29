package situationHandling.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Situation;

public class RuleStorageTest {

	public static void main(String[] args) {

		RuleStorageAccess rsa = StorageAccessFactory.geRuleStorageAccess();

		// test exception handling by adding action to not-existing rule
		rsa.addAction(-1, buildAction());

		ArrayList<Integer> ruleIds = new ArrayList<>();

		// add rule 1
		Situation situation = new Situation("situation1", "object1");
		ruleIds.add(rsa.addRule(situation, buildActionList()));

		// add rule 2
		situation.setSituationName("situation2");
		ruleIds.add(rsa.addRule(situation, buildActionList()));

		// add action to rule 1
		int actionID = rsa.addAction(ruleIds.get(0), buildAction());
		// remove the same action
		rsa.deleteAction(actionID);

		// print all rules
		rsa.getAllRules().forEach(rule -> System.out.println(rule.toString()));

		// print actions of rule 1, get by id
		System.out.println(rsa.getActionsByRuleID(ruleIds.get(0)).toString());

		// print actions of rule 2, get by situation
		System.out.println(rsa.getActionsBySituation(situation).toString());

		// insert action to rule 1, then update it several times
		actionID = rsa.addAction(ruleIds.get(0), buildAction());
		rsa.updateAction(actionID, "neu", null, null, null);
		rsa.updateAction(actionID, null, "neu", null, null);
		rsa.updateAction(actionID, null, null, "neu", null);
		HashMap<String, String> params = new HashMap<>();
		params.put("neu1", "value1");
		params.put("neu2", "value2");

		rsa.updateAction(actionID, null, null, null, params);
		// print actions of rule 1, get by id
		System.out.println(rsa.getActionsByRuleID(ruleIds.get(0)).toString());

		// update rule 1 by id
		situation.setSituationName("situation3");
		rsa.updateRuleSituation(ruleIds.get(0), situation);
		// print all rules
		rsa.getAllRules().forEach(rule -> System.out.println(rule.toString()));

		// update by situation
		rsa.updateRuleSituation(situation, new Situation("situationX",
				"objectY"));
		// print all rules
		rsa.getAllRules().forEach(rule -> System.out.println(rule.toString()));

		// delete all rules
		ruleIds.forEach(i -> rsa.deleteRule(i));

		// print all rules
		System.out.println("Final Print");
		rsa.getAllRules().forEach(rule -> System.out.println(rule.toString()));

		System.exit(0);

	}

	private static List<Action> buildActionList() {
		HashMap<String, String> params = new HashMap<>();
		params.put("param1", "value1");
		params.put("param2", "value2");
		params.put("param3", "value3");

		LinkedList<Action> actions = new LinkedList<>();

		for (int i = 0; i < 3; i++) {
			actions.add(buildAction());
		}

		return actions;
	}

	private static Action buildAction() {
		HashMap<String, String> params = new HashMap<>();
		params.put("param1", "value1");
		params.put("param2", "value2");
		params.put("param3", "value3");
		Action action = new Action("ExamplePlugin", "ExampleAddress",
				"ExampleMessage", params);
		return action;

	}

}

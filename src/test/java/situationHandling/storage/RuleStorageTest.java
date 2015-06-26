package situationHandling.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Situation;

public class RuleStorageTest {

	public static void main(String[] args) {

		RuleStorageAccess rsa = StorageAccessFactory.geRuleStorageAccess();

		ArrayList<Integer> ids = new ArrayList<>();

		HashMap<String, String> params = new HashMap<>();
		params.put("param1", "value1");
		params.put("param2", "value2");
		params.put("param3", "value3");
		Action action = new Action("ExamplePlugin", "ExampleAddress",
				"ExampleMessage", params);
		Situation situation = new Situation("situation1", "object1");

		LinkedList<Action> actions = new LinkedList<>();
		actions.add(action);

		rsa.addRule(situation, actions);
		// ids.add(rsa.addAction(situation, action));

		System.exit(0);

	}

}

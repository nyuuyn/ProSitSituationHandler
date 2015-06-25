package situationHandling.storage;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.map.MultiKeyMap;

//TODO: 1. DummyAction oder das richtige erstetzen
//TODO: 2. Sicherstellen, dass das mit dem Remove usw. auch so geht (Oberflaeche!) --> eine Action muss eindeutig identifizierbar sein

public class RuleStorageAccessImpl {

	private MultiKeyMap<String, LinkedList<DummyAction>> storage;

	public RuleStorageAccessImpl() {
		storage = new MultiKeyMap<String, LinkedList<DummyAction>>();

	}

	// TODO: Weitere Params benoetigt, z.B. fuer SituationHandling abhaengig von
	// Situation
	public void addRule(String situation, String situationObject,
			DummyAction action) {

		LinkedList<DummyAction> actions;
		if (storage.containsKey(situation, situationObject)) {
			actions = storage.get(situation, situationObject);
		} else {
			actions = new LinkedList<DummyAction>();
		}

		actions.add(action);

		storage.put(situation, situationObject, actions);

	}

	public List<DummyAction> getActionsForSituation(String situation,
			String situationObject) {
		if (storage.containsKey(situation, situationObject)) {
			return storage.get(situation, situationObject);
		} else {
			return new LinkedList<DummyAction>();
		}

	}

	public boolean removeAction(String situation, String situationObject,
			DummyAction action) {
		LinkedList<DummyAction> actions = storage.get(situation,
				situationObject);
		return actions.remove(action);
	}

}

package situationHandling.storage;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.map.MultiKeyMap;

// TODO: Auto-generated Javadoc
//TODO: 1. DummyAction oder das richtige erstetzen
//TODO: 2. Sicherstellen, dass das mit dem Remove usw. auch so geht (Oberflaeche!) --> eine Action muss eindeutig identifizierbar sein

//TODO: Diese Klasse löschen

/**
 * The Class RuleStorageAccessImpl_Dummy.
 */
public class RuleStorageAccessImpl_Dummy {

	/** The storage. */
	private MultiKeyMap<String, LinkedList<DummyAction>> storage;

	/**
	 * Instantiates a new rule storage access impl_ dummy.
	 */
	public RuleStorageAccessImpl_Dummy() {
		storage = new MultiKeyMap<String, LinkedList<DummyAction>>();

	}

	// TODO: Weitere Params benoetigt, z.B. fuer SituationHandling abhaengig von
	/**
	 * Adds the rule.
	 *
	 * @param situation the situation
	 * @param situationObject the situation object
	 * @param action the action
	 */
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

	/**
	 * Gets the actions for situation.
	 *
	 * @param situation the situation
	 * @param situationObject the situation object
	 * @return the actions for situation
	 */
	public List<DummyAction> getActionsForSituation(String situation,
			String situationObject) {
		if (storage.containsKey(situation, situationObject)) {
			return storage.get(situation, situationObject);
		} else {
			return new LinkedList<DummyAction>();
		}

	}

	/**
	 * Removes the action.
	 *
	 * @param situation the situation
	 * @param situationObject the situation object
	 * @param action the action
	 * @return true, if successful
	 */
	public boolean removeAction(String situation, String situationObject,
			DummyAction action) {
		LinkedList<DummyAction> actions = storage.get(situation,
				situationObject);
		return actions.remove(action);
	}
	


}

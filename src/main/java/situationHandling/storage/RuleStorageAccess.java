package situationHandling.storage;

import java.util.HashMap;
import java.util.List;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

/**
 * The Interface {@code RuleStorageAccess} gives access to the rule storage.
 * Main purpose of the rule storage is to provide the rules for handling
 * occurring situations. Rules consist of the situation the rules apply to and a
 * set of actions that are executed when this situation occurs. Only one rule is
 * allowed for each situation, but a rule can have more than one actions.
 * 
 * <p>
 * The rule storage also allows manipulation of existing rules and adding new
 * rules.
 * <p>
 * This {@code Interface} allows to add, manipulate and retrieve rules from the
 * rule storage. It defines methods for all interaction with the storage that is
 * required. Instances of Implementing classes can be created using
 * {@link StorageAccessFactory}.
 * 
 * <p>
 * Note that this Interface is only used to access rules and not endpoints. For
 * endpoints see the {@code Interface} {@link EndpointStorageAccess}.
 * 
 * @see Rule
 * @see Situation
 * @see Action
 */
public interface RuleStorageAccess {

	/**
	 * Adds a new rule for the specified situation to the rule storage, using
	 * the specified actions. If a rule for the specified situation already
	 * exists, the specified actions are added to this rule. Thus you can and
	 * should also use this method to add several actions to a rule. For adding
	 * only one action, {@link RuleStorageAccess#addAction(int, Action)} is
	 * recommended.
	 * <p>
	 * The rule will be stored persistently. Furthermore, an unique id is
	 * assigned to the rule. The id can be used to refer to the rule after it
	 * was added.
	 *
	 * @param situation
	 *            the situation in which a rule is used
	 * @param actions
	 *            the actions to perform when the specified situation occurs.
	 * @return the id that was assigned to the rule
	 */
	public int addRule(Situation situation, List<Action> actions);

	/**
	 * Adds the action to the rule with the specified id. Nothing happens, if
	 * the rule does not exist. To add more than one action to a rule or to add
	 * an action to a rule by specifying the situation, it is recommended to use
	 * {@link #addRule(Situation, List)}.
	 * <p>
	 * By adding, an unique id is assigned to the action. The id can be used to
	 * refer to the action after it was added.
	 *
	 * @param ruleID
	 *            the id of the rule to which this action should be added
	 * @param action
	 *            the action to add to the rule
	 * @return the id that was assigned to the action. -1 if no rule was found
	 *         with the specified id.
	 */
	public int addAction(int ruleID, Action action);

	/**
	 * Deletes the action with the specified id from the storage. The action
	 * won't be executed anymore after it was deleted.
	 *
	 * @param actionID
	 *            the id of the action to delete
	 * @return {@code true}, if successful, {@code false} else
	 */
	public boolean deleteAction(int actionID);

	/**
	 * Deletes the rule with the specified id from the storage. Furthermore
	 * deletes all actions that were associated to this rule.
	 *
	 * @param ruleID
	 *            the id of the rule to delete
	 * @return {@code true}, if successful
	 */
	public boolean deleteRule(int ruleID);

	/**
	 * Updates an existing action with the specified id. It is possible to
	 * update the the plugin that is used, the address to use, the payload and
	 * also the params.
	 * <p>
	 * Note that all parameters except {@code actionID} are optional. If no
	 * action with this id exists, nothing happens.
	 *
	 * @param actionID
	 *            the action to update
	 * @param pluginID
	 *            the id of the new plugin to use. If {@code pluginID} is
	 *            {@code null}, the pluginID will not be updated
	 * @param address
	 *            the address of the new recipent. If {@code address} is
	 *            {@code null}, the address will not be updated
	 * @param payload
	 *            the new payload to send. If {@code payload} is {@code null},
	 *            the payload will not be updated
	 * @param params
	 *            the new params. If {@code params} is {@code null}, the params
	 *            will not be updated
	 * @return true, if successful
	 */
	public boolean updateAction(int actionID, String pluginID, String address,
			String payload, HashMap<String, String> params);

	/**
	 * Updates the situation of an existing rule with the specified id. Note
	 * that the update will fail, if there already is another rule with the same
	 * situation. To update the actions of a rule, use
	 * {@link #updateAction(int, String, String, String, HashMap)} or the
	 * methods to add and remove actions.
	 * <p>
	 *
	 * @param ruleID
	 *            the id of the rule to update
	 * @param situation
	 *            the new situation for this rule
	 * @return true, if successful, false if not, for example when there is
	 *         already a rule for the new situation.
	 */
	public boolean updateRuleSituation(int ruleID, Situation situation);

	/**
	 * Updates the situation of an existing rule that is used for
	 * {@code oldSituation}. Note that the update will fail, if there already is
	 * another rule with the same situation than {@code newSituation}. To update
	 * the actions of a rule, use
	 * {@link #updateAction(int, String, String, String, HashMap)} or the
	 * methods to add and remove actions.
	 *
	 * @param oldSituation
	 *            the old situation this rule applied to
	 * @param newSituation
	 *            the new situation the rule should apply to
	 * @return true, if successful, false if not, for example when there is
	 *         already a rule for the new situation.
	 */
	public boolean updateRuleSituation(Situation oldSituation,
			Situation newSituation);

	/**
	 * Gets the all rules that are currently stored.
	 *
	 * @return all available rules. An empty lists if there are no rules in the
	 *         storage
	 */
	public List<Rule> getAllRules();

	/**
	 * Gets the rule with the specified ID.
	 * 
	 * @param ruleID
	 *            the id of the rule to get
	 * @return the rule if it exists, null else
	 */
	public Rule getRuleByID(int ruleID);

	/**
	 * Gets the actions that are associated with the specified situation.
	 *
	 * @param situation
	 *            the situation
	 * @return a list of all actions that are executed when the specified
	 *         situation occurs. An empty list if no actions are available.
	 */
	public List<Action> getActionsBySituation(Situation situation);

	/**
	 * Gets the actions that are associated with the specified rule.
	 *
	 * @param ruleID
	 *            the id of the rule
	 * @return a list of all actions that are associated with the specified
	 *         rule. An empty list if no actions are available.
	 */
	public List<Action> getActionsByRuleID(int ruleID);

	/**
	 * Gets the action with the specified ID.
	 * 
	 * @param actionID
	 *            the id of the action to get
	 * @return the action if it exists, null else
	 */
	public Action getActionByID(int actionID);

}

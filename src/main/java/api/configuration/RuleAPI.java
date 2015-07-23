package api.configuration;

import java.util.List;

import org.apache.camel.Exchange;

import situationHandling.exceptions.InvalidActionException;
import situationHandling.exceptions.InvalidRuleException;
import situationHandling.storage.RuleStorageAccess;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

/**
 * The Class RuleAPI implements the functionality of the rest configuration api
 * for the rules. For each allowed rest-operation, there is a dedicated
 * operation.
 * <p>
 * The class serves as target for the camel route that specifies the rest api
 * methods.
 * 
 * @see Rule
 * @see RuleStorageAccess
 */
public class RuleAPI {

	/** The instance of {@code RuleStorageAccess} to access the storage. */
	private RuleStorageAccess rsa;

	/**
	 * Creates a new instance of RuleAPi and does necessary configuration.
	 */
	public RuleAPI() {
		this.rsa = StorageAccessFactory.getRuleStorageAccess();
	}

	/**
	 * Gets all rules that are currently stored in the rule directory.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param exchange
	 *            the exchange that contains the received message. Also serves
	 *            as container for the answer.
	 * @return The rules as list. If there are no rules, an empty list is
	 *         returned. The return value is stored in the exchange.
	 */
	public void getRules(Exchange exchange) {
		exchange.getIn().setBody(rsa.getAllRules());
	}

	/**
	 * Gets a rule by id from the directory.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param ruleID
	 *            the rule id
	 * 
	 * @param exchange
	 *            the exchange that contains the received message. Also serves
	 *            as container for the answer.
	 * @return The rule. If there is no rule with this id a 404-error is
	 *         returned. The return value is stored in the exchange.
	 */
	public void getRuleByID(Integer ruleID, Exchange exchange) {
		Rule rule = rsa.getRuleByID(ruleID);
		if (rule == null) {
			exchange.getIn().setBody("Rule " + ruleID + " not found.");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		} else {
			exchange.getIn().setBody(rule);
		}
	}

	/**
	 * Adds the rule to the directory.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param exchange
	 *            the exchange that contains the received message. Must contain
	 *            an instance of {@link Rule} in the body. Also serves as
	 *            container for the answer.
	 * @return The id of the new rule. The return value is stored in the
	 *         exchange. A 422-error if an invalid rule is used.
	 */
	public void addRule(Exchange exchange) {
		Rule rule = exchange.getIn().getBody(Rule.class);
		int ruleID;
		try {
			ruleID = rsa.addRule(rule.getSituation(), rule.getActions());
			exchange.getIn().setBody(
					new RestAnswer("Rule successfully added.", String
							.valueOf(ruleID)));
		} catch (InvalidRuleException | InvalidActionException e) {
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setBody(e.getMessage());
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
		}

	}

	/**
	 * Updates the situation a rule applies to.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 *
	 * @param ruleID
	 *            the rule to update
	 * @param exchange
	 *            the exchange that contains the received message. Must contain
	 *            an instance of {@link Situation} in the body. Also serves as
	 *            container for the answer.
	 * @return A 404-error, if there is no rule with the given id or there
	 *         already exists a rule with this id. An 422-error if an invalid
	 *         rule is used
	 */
	public void updateRuleSituation(Integer ruleID, Exchange exchange) {
		Situation situation = exchange.getIn().getBody(Situation.class);

		try {
			if (rsa.updateRuleSituation(ruleID, situation)) {
				exchange.getIn().setBody(
						new RestAnswer("Rule successfully updated", String
								.valueOf(ruleID)));
			} else {
				exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
				exchange.getIn()
						.setBody(
								"Rule "
										+ ruleID
										+ " could not be updated. No rule with this id exists.");
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}
		} catch (InvalidRuleException e) {
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setBody(e.getMessage());
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
		}
	}

	/**
	 * Deletes the rule with the given id from the directory.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param ruleID
	 *            the rule id
	 * @param exchange
	 *            the exchange that contains the received message. Also serves
	 *            as container for the answer.
	 * @return A 404-error, if there is no rule with the given id.
	 */
	public void deleteRule(Integer ruleID, Exchange exchange) {

		if (rsa.deleteRule(ruleID)) {
			exchange.getIn().setBody(
					new RestAnswer("Rule successfully deleted", String
							.valueOf(ruleID)));
		} else {
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn()
					.setBody(
							"Rule "
									+ ruleID
									+ " could not be delete. There is rule with this id.");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		}
	}

	/**
	 * Gets the actions associated with a rule. The rule is specified by the id.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param ruleID
	 *            the rule id
	 * @param exchange
	 *            the exchange that contains the received message. Also serves
	 *            as container for the answer.
	 * @return The actions as list. If there are no actions an empty list is
	 *         returned. If there is no rule with this id, a 404 error is
	 *         returned. The return value is stored in the exchange.
	 */
	public void getActionsByRule(Integer ruleID, Exchange exchange) {
		List<Action> actions = rsa.getActionsByRuleID(ruleID);
		if (actions != null) {
			exchange.getIn().setBody(actions);
		} else {
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setBody("No rule found with id " + ruleID);
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		}

	}

	/**
	 * Adds the action to the directory. The action is associated with the
	 * specified rule.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param ruleID
	 *            the rule id. The action will be executed when this rule is
	 *            used.
	 * @param exchange
	 *            the exchange that contains the received message. Must contain
	 *            an instance of {@link Action} in the body. Also serves as
	 *            container for the answer.
	 * @return A 404-error, if there is no rule with the given id.A 422 error if
	 *         an invalid action was submitted.
	 */
	public void addAction(Integer ruleID, Exchange exchange) {
		Action action = exchange.getIn().getBody(Action.class);
		int actionID;
		try {
			actionID = rsa.addAction(ruleID, action);
			exchange.getIn().setBody(
					new RestAnswer("Action successfully added.", String
							.valueOf(actionID)));
		} catch (InvalidActionException e) {
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setBody(e.getMessage());
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
		} catch (InvalidRuleException e) {
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setBody(e.getMessage());
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		}
	}

	/**
	 * Gets the action by id.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param actionID
	 *            the action id
	 * @param exchange
	 *            the exchange that contains the received message. Also serves
	 *            as container for the answer.
	 * @return the action with this id. An 404-error, if no action with this id
	 *         was found.
	 */
	public void getActionByID(Integer actionID, Exchange exchange) {
		Action action = rsa.getActionByID(actionID);
		if (action == null) {
			exchange.getIn().setBody("Action " + actionID + " not found.");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		} else {
			exchange.getIn().setBody(action);

		}
	}

	/**
	 * Delete the action with this id.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param actionID
	 *            the action id
	 * @param exchange
	 *            the exchange that contains the received message. Also serves
	 *            as container for the answer.
	 * @return A 404-error, if there is no action with the given id.
	 */
	public void deleteAction(Integer actionID, Exchange exchange) {
		if (rsa.deleteAction(actionID)) {
			exchange.getIn().setBody(
					new RestAnswer("Action successfully deleted", String
							.valueOf(actionID)));
		} else {
			exchange.getIn().setBody("Action " + actionID + " not found.");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		}
	}

	/**
	 * Updates the action with the given ID. The parameters of the action are
	 * optional, i.e. they can be null. If a parameter is null, the value is not
	 * updated.
	 * <p>
	 * Target method for a camel route. The exchange is created by camel.
	 * 
	 * @param actionID
	 *            the action id
	 * @param exchange
	 *            the exchange that contains the received message. Must contain
	 *            an instance of {@link Action} in the body. Also serves as
	 *            container for the answer.
	 * @return A 404-error, if there is no action with the given id. A 422 error
	 *         if an invalid action was submitted.
	 */
	public void updateAction(Integer actionID, Exchange exchange) {
		Action action = exchange.getIn().getBody(Action.class);

		try {
			if (rsa.updateAction(actionID.intValue(), action.getPluginID(),
					action.getAddress(), action.getPayload(),
					action.getParams())) {
				exchange.getIn().setBody(
						new RestAnswer("Action successfully updated", String
								.valueOf(actionID)));
			} else {
				exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
				exchange.getIn()
						.setBody(
								"Action "
										+ actionID
										+ " could not be updated. There is no action with this id.");
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}
		} catch (InvalidActionException e) {
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setBody(e.getMessage());
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 422);
		}
	}

}
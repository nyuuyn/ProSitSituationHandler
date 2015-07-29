package situationHandling.storage;

import java.util.List;
import java.util.Map;

import situationHandling.exceptions.InvalidActionException;
import situationHandling.exceptions.InvalidRuleException;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Action.ExecutionTime;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;
import situationManagement.SituationManager;
import situationManagement.SituationManagerFactory;

/**
 * The Class RuleStorageAccessWithSubscribe implements the
 * {@link RuleStorageAccess} Interface. It uses another implementation of
 * the Interface to handle the Database access. Additionally, it takes care of the
 * subscriptions on situations when rules are added/deleted/updated.
 * 
 * @see RuleStorageAccessDefaultImpl
 * @see RuleStorageAccessAdvancedChecks
 * 
 * @author Stefan
 *
 */
class RuleStorageAccessWithSubscribe implements RuleStorageAccess {

	private RuleStorageAccess rsa;

	RuleStorageAccessWithSubscribe(RuleStorageAccess rsa) {
		this.rsa = rsa;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.RuleStorageAccess#addRule(situationHandling
	 * .storage.datatypes.Situation, java.util.List)
	 */
	@Override
	public int addRule(Situation situation, List<Action> actions)
			throws InvalidRuleException, InvalidActionException {

		int id = 0;

		// TODO: Check auf invalide Actions nötig? Wird da ganz abgebrochen?
		try {
			id = rsa.addRule(situation, actions);
		} catch (InvalidRuleException | InvalidActionException e) {
			throw e;
		}
		// subscribe only if adding was successful
		SituationManagerFactory.getSituationManager().subscribeOnSituation(
				situation);

		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#addAction(int,
	 * situationHandling.storage.datatypes.Action)
	 */
	@Override
	public int addAction(int ruleID, Action action)
			throws InvalidActionException, InvalidRuleException {
		return rsa.addAction(ruleID, action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#deleteAction(int)
	 */
	@Override
	public boolean deleteAction(int actionID) {
		return rsa.deleteAction(actionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#deleteRule(int)
	 */
	@Override
	public boolean deleteRule(int ruleID) {

		// check if rule exists and delete subscription of this situation
		Rule rule = rsa.getRuleByID(ruleID);
		if (rule != null) {
			SituationManagerFactory.getSituationManager().removeSubscription(
					rule.getSituation());
		}
		return rsa.deleteRule(ruleID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#updateAction(int,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * situationHandling.storage.datatypes.Action.ExecutionTime, java.util.Map)
	 */
	@Override
	public boolean updateAction(int actionID, String pluginID, String address,
			String payload, ExecutionTime executionTime,
			Map<String, String> params) throws InvalidActionException {

		return rsa.updateAction(actionID, pluginID, address, payload,
				executionTime, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#updateRuleSituation(int,
	 * situationHandling.storage.datatypes.Situation)
	 */
	@Override
	public boolean updateRuleSituation(int ruleID, Situation situation)
			throws InvalidRuleException {
		boolean success = false;
		Situation oldSituation = rsa.getRuleByID(ruleID).getSituation();
		try {
			success = updateRuleSituation(ruleID, situation);
		} catch (InvalidRuleException e) {
			throw e;
		}
		if (success) {
			SituationManager situationManager = SituationManagerFactory
					.getSituationManager();
			// TODO: Mal prüfen, ob nach dem Update die oldSituation nicht
			// automatisch durch Hibernate mit geupdated wird
			situationManager.removeSubscription(oldSituation);
			situationManager.subscribeOnSituation(situation);
		}
		return success;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#updateRuleSituation(
	 * situationHandling.storage.datatypes.Situation,
	 * situationHandling.storage.datatypes.Situation)
	 */
	@Override
	public boolean updateRuleSituation(Situation oldSituation,
			Situation newSituation) throws InvalidRuleException {
		// update subscriptions only if successful, so check for errors and
		// success return value
		boolean success = false;
		try {
			success = updateRuleSituation(oldSituation, newSituation);
		} catch (InvalidRuleException e) {
			throw e;
		}
		if (success) {
			SituationManager situationManager = SituationManagerFactory
					.getSituationManager();
			situationManager.removeSubscription(oldSituation);
			situationManager.subscribeOnSituation(newSituation);
		}

		return success;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getAllRules()
	 */
	@Override
	public List<Rule> getAllRules() {
		return rsa.getAllRules();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getRuleByID(int)
	 */
	@Override
	public Rule getRuleByID(int ruleID) {
		return rsa.getRuleByID(ruleID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getActionsBySituation(
	 * situationHandling.storage.datatypes.Situation)
	 */
	@Override
	public List<Action> getActionsBySituation(Situation situation) {
		return rsa.getActionsBySituation(situation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#
	 * getActionsBySituationAndExecutionTime
	 * (situationHandling.storage.datatypes.Situation,
	 * situationHandling.storage.datatypes.Action.ExecutionTime)
	 */
	@Override
	public List<Action> getActionsBySituationAndExecutionTime(
			Situation situation, ExecutionTime executionTime) {
		return rsa.getActionsBySituationAndExecutionTime(situation,
				executionTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getActionsByRuleID(int)
	 */
	@Override
	public List<Action> getActionsByRuleID(int ruleID) {
		return rsa.getActionsByRuleID(ruleID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getActionByID(int)
	 */
	@Override
	public Action getActionByID(int actionID) {
		return rsa.getActionByID(actionID);
	}

}

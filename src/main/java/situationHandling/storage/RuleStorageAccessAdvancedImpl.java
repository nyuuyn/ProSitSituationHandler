package situationHandling.storage;

import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import situationHandling.exceptions.InvalidActionException;
import situationHandling.exceptions.InvalidRuleException;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Situation;

class RuleStorageAccessAdvancedImpl extends RuleStorageAccessDefaultImpl {

	RuleStorageAccessAdvancedImpl(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public int addRule(Situation situation, List<Action> actions)
			throws InvalidRuleException, InvalidActionException {

		for (Action action : actions) {
			new ActionValidityChecker(action.getParams(), action.getPluginID())
					.checkAction();
		}
		return super.addRule(situation, actions);
	}

	@Override
	public int addAction(int ruleID, Action action)
			throws InvalidActionException {
		new ActionValidityChecker(action.getParams(), action.getPluginID())
				.checkAction();
		return super.addAction(ruleID, action);
	}

	@Override
	public boolean updateAction(int actionID, String pluginID, String address,
			String payload, Map<String, String> params)
			throws InvalidActionException {
		new ActionValidityChecker(params, pluginID).checkAction();
		return super.updateAction(actionID, pluginID, address, payload, params);
	}

}

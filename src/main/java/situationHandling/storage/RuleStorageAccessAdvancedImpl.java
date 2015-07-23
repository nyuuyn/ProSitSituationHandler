package situationHandling.storage;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import situationHandling.exceptions.InvalidActionException;
import situationHandling.exceptions.InvalidRuleException;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Situation;

/**
 * 
 * Implements the {@link RuleStorageAccess}. Does pretty much the same than
 * {@link RuleStorageAccessDefaultImpl} but does more checks on the validity
 * of the input.
 * 
 * @author Stefan
 *
 */
class RuleStorageAccessAdvancedImpl extends RuleStorageAccessDefaultImpl {

	/**
	 * Instantiates a new rule storage access advanced impl.
	 *
	 * @param sessionFactory The session factory used to create database sessions.
	 */
	RuleStorageAccessAdvancedImpl(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/* (non-Javadoc)
	 * @see situationHandling.storage.RuleStorageAccessDefaultImpl#addRule(situationHandling.storage.datatypes.Situation, java.util.List)
	 */
	@Override
	public int addRule(Situation situation, List<Action> actions)
			throws InvalidRuleException, InvalidActionException {

		// we check the new rule for invalid actions. If one action is invalid,
		// we just remove the action and go on.
		Iterator<Action> it = actions.iterator();
		while (it.hasNext()) {
			Action action = it.next();
			try {
				new ActionValidityChecker(action.getParams(),
						action.getPluginID()).checkAction();
			} catch (InvalidActionException e) {
				it.remove();
			}
		}

		return super.addRule(situation, actions);
	}

	/* (non-Javadoc)
	 * @see situationHandling.storage.RuleStorageAccessDefaultImpl#addAction(int, situationHandling.storage.datatypes.Action)
	 */
	@Override
	public int addAction(int ruleID, Action action)
			throws InvalidActionException, InvalidRuleException {

		new ActionValidityChecker(action.getParams(), action.getPluginID())
				.checkAction();

		return super.addAction(ruleID, action);
	}

	/* (non-Javadoc)
	 * @see situationHandling.storage.RuleStorageAccessDefaultImpl#updateAction(int, java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public boolean updateAction(int actionID, String pluginID, String address,
			String payload, Map<String, String> params)
			throws InvalidActionException {
		new ActionValidityChecker(params, pluginID).checkAction();
		return super.updateAction(actionID, pluginID, address, payload, params);
	}

}

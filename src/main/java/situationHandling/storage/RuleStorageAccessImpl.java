package situationHandling.storage;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

class RuleStorageAccessImpl implements RuleStorageAccess {

	private final static Logger logger = Logger
			.getLogger(RuleStorageAccess.class);

	@Override
	public int addRule(Situation situation, List<Action> actions) {
		logger.debug("Adding rule and action.");

		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		Integer ruleID = null;
		try {
			tx = session.beginTransaction();

			Rule rule = getRuleBySituation(situation);
			if (rule == null) {
				rule = new Rule(situation, actions);
				System.out.println("RuleID: " + (Integer) session.save(rule));
			} else {
				for (Action action : actions) {
					rule.addAction(action);
				}
				System.out.println("Update");
				session.update(rule);
			}
			ruleID = rule.getId();

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		logger.debug("Rule added. ID = " + ruleID);
		actions.forEach(action -> logger.debug("Action added. ID = "
				+ action.getId()));

		return ruleID;
	}

	@Override
	public int addAction(int ruleID, Action action) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean removeAction(int ruleID, int ActionID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeRule(int ruleID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateAction(int actionID, String pluginID, String address,
			String message, HashMap<String, String> params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateRule(int ruleID, String situationName,
			String objectName, List<Action> actions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Rule> getAllRules() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Action> getActionsBySituation(Situation situation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Action> getActionsByRuleID(int ruleID) {
		// TODO Auto-generated method stub
		return null;
	}

	private Rule getRuleBySituation(Situation situation) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		Rule rule = null;

		try {
			tx = session.beginTransaction();

			@SuppressWarnings("rawtypes")
			List rules = session
					.createCriteria(Rule.class)
					.add(Restrictions.eq("situationName",
							situation.getSituationName()))
					.add(Restrictions.eq("objectName",
							situation.getObjectName())).list();

			// there is a maximum of one result for this query, since the
			// combination of situationName and objectName is unique in this
			// DB table
			if (rules.size() == 1) {
				rule = (Rule) rules.iterator().next();
				Hibernate.initialize(rule.getActions());

			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return rule;
	}

}

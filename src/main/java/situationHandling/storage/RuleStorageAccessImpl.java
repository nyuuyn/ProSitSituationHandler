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
	public int addAction(Situation situation, Action action) {
		logger.debug("Adding rule and action.");

		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		Integer ruleID = null;
		Integer actionID = null;
		try {
			tx = session.beginTransaction();

			Rule rule = getRuleBySituation(situation);
			if (rule == null) {
				rule = new Rule(situation);
				rule.addAction(action);
				System.out.println("RuleID: " + (Integer) session.save(rule));
			} else{		
				rule.addAction(action);
				System.out.println("Update");
				session.update(rule);
			}
			System.out.println("Rule: " + rule.getId());
			System.out.println("Action: " + action.getId());

			// Rule rule = new Rule(situation);
			// ruleID = getRuleID(rule);

			// // store rule in db, if not available so far
			// if (ruleID == null) {
			// ruleID = (Integer) session.save(rule);
			// }
			//
			// //add new action to rule
			// action.setRuleID(ruleID);
			// actionID = (Integer) session.save(action);

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		logger.debug("Rule added. ID = " + ruleID);
		logger.debug("Action added. ID = " + actionID);
		//TODO
		return 0;
	}

	@Override
	public boolean removeAction(Situation situation, int id) {
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
	public List<Action> getActionsBySituation(Situation situation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Action> getAllActions() {
		// TODO Auto-generated method stub
		return null;
	}

	private Integer getRuleID(Rule rule) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		Integer ruleID = null;

		try {
			tx = session.beginTransaction();

			@SuppressWarnings("rawtypes")
			List rules = session
					.createCriteria(Rule.class)
					.add(Restrictions.eq("situationName",
							rule.getSituationName()))
					.add(Restrictions.eq("objectName", rule.getObjectName()))
					.list();

			// there is a maximum of one result for this query, since the
			// combination of situationName and objectName is unique in this
			// DB table
			if (rules.size() == 1) {
				ruleID = ((Rule) rules.iterator().next()).getId();
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return ruleID;
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

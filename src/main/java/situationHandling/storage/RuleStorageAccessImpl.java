package situationHandling.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
		logger.debug("Adding rule and actions.");

		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		Integer ruleID = null;
		try {
			tx = session.beginTransaction();

			Rule rule = getRuleBySituation(situation);
			if (rule == null) {
				rule = new Rule(situation, actions);
				session.save(rule);
			} else {
				for (Action action : actions) {
					rule.addAction(action);
				}
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
		logger.debug("Rule ID = " + ruleID);
		actions.forEach(action -> logger.debug("Action added. ID = "
				+ action.getId()));

		return ruleID;
	}

	@Override
	public int addAction(int ruleID, Action action) {
		logger.debug("Adding actions.");

		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			// get rule and add action
			Rule rule = ((Rule) session.get(Rule.class, ruleID));
			rule.addAction(action);
			session.update(rule);

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		logger.debug("Action added. ID = " + action.getId());

		return action.getId();
	}

	@Override
	public boolean deleteAction(int actionID) {
		logger.debug("Deleting action: " + actionID);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Action action = (Action) session.get(Action.class, actionID);
			session.delete(action);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			return false;
		} finally {
			session.close();
		}
		return true;
	}

	@Override
	public boolean deleteRule(int ruleID) {
		logger.debug("Deleting rule: " + ruleID);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Rule rule = (Rule) session.get(Rule.class, ruleID);
			session.delete(rule);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			return false;
		} finally {
			session.close();
		}
		return true;
	}

	@Override
	public boolean updateAction(int actionID, String pluginID, String address,
			String message, HashMap<String, String> params) {
		logger.debug("Updating action: " + actionID);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Action action = (Action) session.get(Action.class, actionID);

			if (pluginID != null) {
				action.setPluginID(pluginID);
			}
			if (address != null) {
				action.setAddress(address);
			}
			if (message != null) {
				action.setMessage(message);
			}
			if (params != null) {
				action.setParams(params);
			}

			session.update(action);

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			return false;
		} finally {
			session.close();
		}
		return true;
	}

	@Override
	public boolean updateRuleSituation(int ruleID, Situation situation) {
		logger.debug("Updating rule: " + ruleID + " to new Situation "
				+ situation);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			Rule rule = (Rule) session.get(Rule.class, ruleID);

			rule.setSituation(situation);

			session.update(rule);

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			return false;
		} finally {
			session.close();
		}
		return true;
	}

	@Override
	public boolean updateRuleSituation(Situation oldSituation,
			Situation newSituation) {
		logger.debug("Updating rule from situation: " + oldSituation + " to new Situation "
				+ newSituation);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			
			@SuppressWarnings("rawtypes")
			List rules = session.createQuery(
					"FROM Rule R WHERE R.situationName =  '"
							+ oldSituation.getSituationName()
							+ "' AND R.objectName =  '"
							+ oldSituation.getObjectName() + "'").list();
			if (rules.size() == 1) {
				Rule rule = (Rule) rules.get(0);
				rule.setSituation(newSituation);
				session.update(rule);
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			return false;
		} finally {
			session.close();
		}
		return true;
	}

	@Override
	public List<Rule> getAllRules() {
		logger.debug("Getting all rules");

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		LinkedList<Rule> rules = new LinkedList<>();
		try {
			tx = session.beginTransaction();

			@SuppressWarnings("rawtypes")
			List queryResults = session.createQuery("FROM Rule").list();

			@SuppressWarnings("rawtypes")
			Iterator it = queryResults.iterator();

			while (it.hasNext()) {
				Rule rule = (Rule) it.next();
				Hibernate.initialize(rule.getActions());
				rule.getActions().forEach(
						action -> Hibernate.initialize(action.getParams()));
				rules.add(rule);
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return rules;
	}

	@Override
	public List<Action> getActionsBySituation(Situation situation) {
		logger.debug("Getting all actions for situation: " + situation);

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		Rule rule = null;
		try {
			tx = session.beginTransaction();

			@SuppressWarnings("rawtypes")
			List rules = session.createQuery(
					"FROM Rule R WHERE R.situationName =  '"
							+ situation.getSituationName()
							+ "' AND R.objectName =  '"
							+ situation.getObjectName() + "'").list();
			if (rules.size() == 1) {
				rule = (Rule) rules.get(0);
			}

			tx.commit();
			return getRuleActions(rule, session);
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}

	@Override
	public List<Action> getActionsByRuleID(int ruleID) {
		logger.debug("Getting all actions of rule: " + ruleID);

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		Rule rule = null;
		try {
			tx = session.beginTransaction();

			rule = (Rule) session.get(Rule.class, ruleID);

			tx.commit();
			return getRuleActions(rule, session);
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
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

	private List<Action> getRuleActions(Rule rule, Session session) {
		Transaction tx = null;
		List<Action> actions = new LinkedList<>();
		try {
			tx = session.beginTransaction();

			Hibernate.initialize(rule.getActions());
			actions = rule.getActions();
			actions.forEach(action -> Hibernate.initialize(action.getParams()));

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
		return actions;
	}

}

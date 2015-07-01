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

/**
 * The Class RuleStorageAccessImpl provides the standard implementation for the
 * {@code Interface} {@link RuleStorageAccess}. It uses a relational SQL
 * database to store the rules. To access the database JPA 2.0/Hibernate is
 * used.
 */
class RuleStorageAccessImpl implements RuleStorageAccess {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(RuleStorageAccess.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.RuleStorageAccess#addRule(situationHandling
	 * .storage.datatypes.Situation, java.util.List)
	 */
	@Override
	public int addRule(Situation situation, List<Action> actions) {
		logger.debug("Adding rule and actions.");

		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		Integer ruleID = null;
		try {
			tx = session.beginTransaction();

			Rule rule = null;

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

			if (rule == null) {
				rule = new Rule(situation, actions);
				session.save(rule);
			}
			// rule already exists. Append actions
			else {
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
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		logger.debug("Rule ID = " + ruleID);
		actions.forEach(action -> logger.debug("Action added. ID = "
				+ action.getId()));

		return ruleID;
	}

	// TODO: Batch add? Klingt zwar sinnvoll, wird praktisch aber wohl nie
	// ben�tigt?

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#addAction(int,
	 * situationHandling.storage.datatypes.Action)
	 */
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
			logger.error("Hibernate error", e);
			return -1;
		} catch (NullPointerException e) {
			logger.info("Rule with id " + ruleID
					+ " not found. No action added.");
			return -1;
		} finally {
			session.close();
		}
		logger.debug("Action added. ID = " + action.getId());

		return action.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#deleteAction(int)
	 */
	@Override
	public boolean deleteAction(int actionID) {
		logger.debug("Deleting action: " + actionID);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Action action = (Action) session.get(Action.class, actionID);
			if (action == null) {
				logger.info("No action with ID " + actionID
						+ " found. No action deleted.");
				return false;
			} else {
				session.delete(action);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
			return false;
		} finally {
			session.close();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#deleteRule(int)
	 */
	@Override
	public boolean deleteRule(int ruleID) {
		logger.debug("Deleting rule: " + ruleID);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Rule rule = (Rule) session.get(Rule.class, ruleID);
			if (rule == null) {
				logger.info("No rule with ID " + ruleID
						+ " found. No rule deleted.");
				return false;
			} else {
				session.delete(rule);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
			return false;
		} finally {
			session.close();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#updateAction(int,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.HashMap)
	 */
	@Override
	public boolean updateAction(int actionID, String pluginID, String address,
			String payload, HashMap<String, String> params) {
		logger.debug("Updating action: " + actionID);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Action action = (Action) session.get(Action.class, actionID);

			if (action == null) {
				logger.info("No action with ID " + actionID
						+ " found. No action updated.");
				return false;
			} else {

				if (pluginID != null) {
					action.setPluginID(pluginID);
				}
				if (address != null) {
					action.setAddress(address);
				}
				if (payload != null) {
					action.setPayload(payload);
				}
				if (params != null) {
					action.setParams(params);
				}

				session.update(action);
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
			return false;
		} finally {
			session.close();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#updateRuleSituation(int,
	 * situationHandling.storage.datatypes.Situation)
	 */
	@Override
	public boolean updateRuleSituation(int ruleID, Situation situation) {
		logger.debug("Updating rule: " + ruleID + " to new Situation "
				+ situation);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			Rule rule = (Rule) session.get(Rule.class, ruleID);

			if (rule == null) {
				logger.info("No rule with ID " + ruleID
						+ " found. No rule updated.");
				return false;
			} else {
				rule.setSituation(situation);
				session.update(rule);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
			return false;
		} finally {
			session.close();
		}
		return true;
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
			Situation newSituation) {
		logger.debug("Updating rule from situation: " + oldSituation
				+ " to new Situation " + newSituation);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			// query for rule with old situation
			@SuppressWarnings("rawtypes")
			List rules = session
					.createCriteria(Rule.class)
					.add(Restrictions.eq("situationName",
							oldSituation.getSituationName()))
					.add(Restrictions.eq("objectName",
							oldSituation.getObjectName())).list();
			// there is one rule rule with this situation or none
			if (rules.size() == 1) {
				Rule rule = (Rule) rules.get(0);
				rule.setSituation(newSituation);
				session.update(rule);
			} else {
				logger.info("No rule for situation " + oldSituation.toString()
						+ " found. No rule updated.");
				return false;
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
			return false;
		} finally {
			session.close();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getAllRules()
	 */
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

			// also load actions and params from database
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
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		return rules;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getRuleByID( int )
	 */
	@Override
	public Rule getRuleByID(int ruleID) {
		logger.debug("Getting rule with id " + ruleID);

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		Rule rule = null;
		try {
			tx = session.beginTransaction();

			rule = (Rule) session.get(Rule.class, ruleID);

			if (rule != null) {
				// also load actions and params from database
				Hibernate.initialize(rule.getActions());
				rule.getActions().forEach(
						action -> Hibernate.initialize(action.getParams()));
			} else {
				logger.info("No rule found with id = " + ruleID);
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		return rule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getActionsBySituation(
	 * situationHandling.storage.datatypes.Situation)
	 */
	@Override
	public List<Action> getActionsBySituation(Situation situation) {
		logger.debug("Getting all actions for situation: " + situation);

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
			if (rules.size() == 1) {
				rule = (Rule) rules.get(0);
			}

			tx.commit();
			return getInitializedRuleActions(rule, session);
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		return new LinkedList<Action>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getActionsByRuleID(int)
	 */
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
			return getInitializedRuleActions(rule, session);
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		return new LinkedList<Action>();
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getActionByID( int )
	 */
	@Override
	public Action getActionByID(int actionID) {
		logger.debug("Getting Action with id " + actionID);

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		Action action = null;
		try {
			tx = session.beginTransaction();

			action = (Action) session.get(Action.class, actionID);

			if (action != null) {
				// also load params from database
				Hibernate.initialize(action.getParams());
			} else {
				logger.info("No action found with id = " + actionID);
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		return action;
	}

	/**
	 * Helper method to load all actions of a rule and also their params from
	 * the database, i.e. to fully initialize the rule.
	 * 
	 * @param rule
	 *            the rule that should be loaded
	 * @param session
	 *            the session is used to load the actions and params from the
	 *            database. Use the same session that was used to load the rule
	 *            from the database.
	 * @return a list that contains the initialized actions
	 */
	private List<Action> getInitializedRuleActions(Rule rule, Session session) {
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
			logger.error("Hibernate error", e);
		}
		return actions;
	}


}

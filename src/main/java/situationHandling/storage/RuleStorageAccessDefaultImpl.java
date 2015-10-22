package situationHandling.storage;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import situationHandling.exceptions.InvalidActionException;
import situationHandling.exceptions.InvalidRuleException;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Action.ExecutionTime;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

/**
 * The Class RuleStorageAccessImpl provides the standard implementation for the
 * {@code Interface} {@link RuleStorageAccess}. It uses a relational SQL
 * database to store the rules. To access the database JPA 2.0/Hibernate is
 * used. <br>
 * 
 * The DefaultImpl does only minimal checks on the semantic validity of the
 * inputs and handles errors on database level.
 */
class RuleStorageAccessDefaultImpl implements RuleStorageDatabase {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(RuleStorageAccessDefaultImpl.class);

	/**
	 * The session factory used to create database sessions.
	 */
	private SessionFactory sessionFactory;

	/**
	 * Creates a new instance of RuleStorageAccessImpl.
	 * 
	 * @param sessionFactory
	 *            The session factory used to create database sessions.
	 */
	RuleStorageAccessDefaultImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
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
		logger.debug("Adding rule and actions.");

		Session session = sessionFactory.openSession();

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
							situation.getObjectId())).list();

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
		} catch (JDBCException e) {
			if (tx != null)
				tx.rollback();
			throw new InvalidRuleException(createErrorMessage(e, "Rule"), e);
		} finally {
			session.close();
		}
		logger.debug("Rule ID = " + ruleID);
		actions.forEach(action -> logger.debug("Action added. ID = "
				+ action.getId()));

		return ruleID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.RuleStorageAccess#ruleExists(situationHandling.
	 * storage.datatypes.Situation)
	 */
	@Override
	public boolean ruleExists(Situation situation) {
		logger.debug("Checking rule existence for: " + situation.toString());
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		boolean exists = false;
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("rawtypes")
			List rules = session
					.createCriteria(Rule.class)
					.add(Restrictions.eq("situationName",
							situation.getSituationName()))
					.add(Restrictions.eq("objectName",
							situation.getObjectId())).list();
			if (rules.size() == 1) {
				exists = true;
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);

		} finally {
			session.close();
		}
		return exists;
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
		logger.debug("Adding actions.");

		Session session = sessionFactory.openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			// get rule and add action
			Rule rule = ((Rule) session.get(Rule.class, ruleID));
			rule.addAction(action);
			session.update(rule);

			tx.commit();
		} catch (JDBCException e) {
			if (tx != null)
				tx.rollback();
			throw new InvalidActionException(createErrorMessage(e, "Action"), e);
		} catch (NullPointerException e) {
			logger.info("Rule with id " + ruleID
					+ " not found. No action added.");
			throw new InvalidRuleException("Rule with id " + ruleID
					+ " not found. No action added.", e);
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
		Session session = sessionFactory.openSession();
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
	 * @see
	 * situationHandling.storage.RuleStorageAccess#deleteActionsByPlugin(java
	 * .lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean deleteActionsByPlugin(String pluginID) {
		logger.debug("Deleting all actions that are executed by plugin: "
				+ pluginID);

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			List actions = session.createCriteria(Action.class)
					.add(Restrictions.eq("pluginID", pluginID)).list();

			actions.forEach(action -> session.delete(action));

			tx.commit();
			return actions.size() > 0;
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
			return false;
		} finally {
			session.close();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#deleteRule(int)
	 */
	@Override
	public boolean deleteRule(int ruleID) {
		logger.debug("Deleting rule: " + ruleID);
		Session session = sessionFactory.openSession();
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
			String payload, ExecutionTime executionTime,
			Map<String, String> params) throws InvalidActionException {
		logger.debug("Updating action: " + actionID);
		Session session = sessionFactory.openSession();
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
				if (executionTime != null) {
					action.setExecutionTime(executionTime);
				}

				session.update(action);
			}

			tx.commit();
		} catch (JDBCException e) {
			if (tx != null)
				tx.rollback();
			throw new InvalidActionException(createErrorMessage(e, "Action"), e);
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
	public boolean updateRuleSituation(int ruleID, Situation situation)
			throws InvalidRuleException {
		logger.debug("Updating rule: " + ruleID + " to new Situation "
				+ situation);
		Session session = sessionFactory.openSession();
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
		} catch (JDBCException e) {
			if (tx != null)
				tx.rollback();
			throw new InvalidRuleException(createErrorMessage(e, "Rule"), e);
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
			Situation newSituation) throws InvalidRuleException {
		logger.debug("Updating rule from situation: " + oldSituation
				+ " to new Situation " + newSituation);
		Session session = sessionFactory.openSession();
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
							oldSituation.getObjectId())).list();
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
		} catch (JDBCException e) {
			if (tx != null)
				tx.rollback();
			throw new InvalidRuleException(createErrorMessage(e, "Rule"), e);
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

		Session session = sessionFactory.openSession();
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

		Session session = sessionFactory.openSession();
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

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<Action> actions = null;
		try {
			tx = session.beginTransaction();

			@SuppressWarnings("rawtypes")
			List rules = session
					.createCriteria(Rule.class)
					.add(Restrictions.eq("situationName",
							situation.getSituationName()))
					.add(Restrictions.eq("objectName",
							situation.getObjectId())).list();

			tx.commit();
			// there is max one rule for this situation (or there isn't a rule
			// for this situation)
			if (rules.size() == 1) {
				actions = getInitializedRuleActions((Rule) rules.get(0),
						session);
			}
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		return actions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.RuleStorageAccess#getActionsBySituationAndState
	 * (situationHandling.storage.datatypes.Situation, boolean)
	 */
	@Override
	public List<Action> getActionsBySituationAndExecutionTime(
			Situation situation, ExecutionTime executionTime) {

		logger.debug("Getting all actions for situation: " + situation
				+ " with execution time " + executionTime.toString());

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<Action> actions = null;
		try {
			tx = session.beginTransaction();

			SQLQuery query = session
					.createSQLQuery("SELECT actions.* FROM rules, actions WHERE rules.id = actions.rule_id AND rules.situation_name = '"
							+ situation.getSituationName()
							+ "' AND rules.object_id = '"
							+ situation.getObjectId()
							+ "' AND (actions.execution_time = '"
							+ executionTime
							+ "' OR actions.execution_time = 'onSituationChange')");
			query.addEntity(Action.class);
			@SuppressWarnings("rawtypes")
			List result = query.list();

			// alternative to the above expression that does not work
			// @SuppressWarnings("rawtypes")
			// List rules = session
			// .createCriteria(Rule.class)
			// .add(Restrictions.eq("situationName",
			// situation.getSituationName()))
			// .add(Restrictions.eq("objectName",
			// situation.getObjectName()))
			// .createAlias(
			// "actions",
			// "act",
			// JoinType.INNER_JOIN,
			// Restrictions.or(Restrictions.eq(
			// "act.executionTime",
			// ExecutionTime.onSituationChange),
			// Restrictions.eq("act.executionTime",
			// executionTime))).list();

			actions = new LinkedList<>();
			for (Object o : result) {
				Action action = (Action) o;
				Hibernate.initialize(action.getParams());
				actions.add(action);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		return actions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getActionsByRuleID(int)
	 */
	@Override
	public List<Action> getActionsByRuleID(int ruleID) {
		logger.debug("Getting all actions of rule: " + ruleID);

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Rule rule = null;
		List<Action> actions = null;
		try {
			tx = session.beginTransaction();

			rule = (Rule) session.get(Rule.class, ruleID);

			tx.commit();
			if (rule != null) {
				actions = getInitializedRuleActions(rule, session);
			}

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		return actions;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.RuleStorageAccess#getActionByID( int )
	 */
	@Override
	public Action getActionByID(int actionID) {
		logger.debug("Getting Action with id " + actionID);

		Session session = sessionFactory.openSession();
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

	/**
	 * Conience Method to create error messages for JDBC Exceptions
	 * 
	 * @param e
	 *            the exception
	 * @param subject
	 *            "rule" or "action"
	 * @return A nicely readable error message.
	 */
	private String createErrorMessage(JDBCException e, String subject) {
		String errorMessage;
		if (e.getErrorCode() == 1048) {// column not set
			errorMessage = subject
					+ " property not set. Please set all properties.";
		} else if (e.getErrorCode() == 1062) {// duplicate
			errorMessage = "Duplicate " + subject
					+ ". There exists already an identical " + subject + ".";
		} else {// unknown
			errorMessage = "Unknown error when creating " + subject + ".";
		}
		logger.debug(errorMessage);
		return errorMessage;
	}

}

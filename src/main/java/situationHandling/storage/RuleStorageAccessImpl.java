package situationHandling.storage;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Endpoint;
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
			Rule rule = new Rule(situation);
			session.save(rule);
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
		return actionID;
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

}

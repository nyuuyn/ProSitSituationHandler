
package situationHandling.storage;

import java.util.List;

import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.HistoryEntry;
import situationHandling.storage.datatypes.Situation;

/**
 * 
 * TODO
 * 
 * @author Stefan
 *
 */
public class HistoryAccess {

	/**
	 * The session factory used to create database sessions.
	 */
	private SessionFactory sessionFactory;

	/**
	 * @param sessionFactory
	 */
	HistoryAccess(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void appendAction(Action action, Situation situation, boolean state) {
		// TODO: Das am besten asynchron machen

		String situationString = "Situation defined by SituationTemplate: " + situation.getSituationName()
				+ " and Thing: " + situation.getObjectName() + " has " + (state ? "appeared" : "disappeared");
		String typeOfAction = "Notification with: " + action.getPluginID();

		Session session = sessionFactory.openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			// Endpoint endpoint = new Endpoint(endpointURL, situations,
			// operation);
			HistoryEntry entry = new HistoryEntry(situationString, typeOfAction, action.getAddress(),
					action.getPayload(), "Used Parameters: " + action.getParams().toString());
			session.save(entry);
			tx.commit();
		} catch (JDBCException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void appendWorkflowOperation(Endpoint endpoint, boolean success) {
		// TODO: Das am besten asynchron machen

		StringBuilder situationString = new StringBuilder();
		for (HandledSituation handledSituation : endpoint.getSituations()) {
			situationString.append("Situation defined by SituationTemplate: " + handledSituation.getSituationName()
					+ " and Thing: " + handledSituation.getObjectName() + " is " + handledSituation.isSituationHolds());
		}

		String typeOfAction = "Workflow operation : " + endpoint.getQualifier() + " : " + endpoint.getOperationName();
		String payload = "";
		String recipent = "Endpoint: " + endpoint.getEndpointID() + " with address " + endpoint.getEndpointURL();
		String misc = "Invocation " + (success ? "successful" : "failed");

		Session session = sessionFactory.openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			// Endpoint endpoint = new Endpoint(endpointURL, situations,
			// operation);
			HistoryEntry entry = new HistoryEntry(situationString.toString(), typeOfAction, recipent, payload, misc);
			session.save(entry);
			tx.commit();
		} catch (JDBCException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

	}

	public List<HistoryEntry> getHistory(int offset, int numberOfEntries) {
		return null;
	}

}

package situationHandling.storage;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.HistoryEntry;
import situationHandling.storage.datatypes.Situation;

/**
 * The class {@link HistoryAccess} provides access to the history of actions and
 * chosen workflow endpoints. The history is stored in a persistent way. The
 * class provides means to create new entries in the history and to query the
 * history.
 *
 * @author Stefan
 */
public class HistoryAccess {

	/** The logger. */
	private Logger logger = Logger.getLogger(HistoryAccess.class);

	/**
	 * The session factory used to create database sessions.
	 */
	private final SessionFactory sessionFactory;

	/**
	 * Instantiates a new history access.
	 *
	 * @param sessionFactory
	 *            the session factory to access the database
	 */
	HistoryAccess(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Append an action to the history. The action is added to the history in an
	 * asynchronous way, i.e. calling this method does not immediately add the
	 * action to the history. If the situation handler fails after calling this
	 * method, there is no guarantee that the action is added to the history.
	 *
	 * @param action
	 *            the action to append
	 * @param situation
	 *            the situation in which the action was used
	 * @param state
	 *            the state of the situation, i.e. if it occured (true) or not
	 *            (false).
	 */
	public void appendAction(Action action, Situation situation, boolean state) {
		// TODO: gut das so zu machen?
		// write history asynchronously in thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.debug("Creating action history entry:" + situation
						+ action);

				String situationString = "Situation defined by SituationTemplate: "
						+ situation.getSituationName()
						+ " and Thing: "
						+ situation.getObjectName()
						+ " has "
						+ (state ? "appeared" : "disappeared");
				String typeOfAction = "Notification with: "
						+ action.getPluginID();

				Session session = sessionFactory.openSession();

				Transaction tx = null;

				try {
					tx = session.beginTransaction();
					HistoryEntry entry = new HistoryEntry(situationString,
							typeOfAction, action.getAddress(),
							action.getPayload(), "Used Parameters: "
									+ action.getParams().toString());
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
		}).start();
	}

	/**
	 * Append an workflow operation to the history. The workflow operation is
	 * added to the history in an asynchronous way, i.e. calling this method
	 * does not immediately add the workflow operation to the history. If the
	 * situation handler fails after calling this method, there is no guarantee
	 * that the workflow operation is added to the history.
	 *
	 * @param endpoint
	 *            the endpoint that was used to execute the operation
	 * @param success
	 *            true if the operation was executed successfully, false else
	 */
	public void appendWorkflowOperation(Endpoint endpoint, boolean success) {
		// TODO: gut das so zu machen?
		// write history asynchronously in thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				logger.debug("Creating workflow history entry: " + endpoint);
				StringBuilder situationString = new StringBuilder();
				for (HandledSituation handledSituation : endpoint
						.getSituations()) {
					situationString
							.append("Situation defined by SituationTemplate: "
									+ handledSituation.getSituationName()
									+ " and Thing: "
									+ handledSituation.getObjectName() + " is "
									+ handledSituation.isSituationHolds());
				}

				String typeOfAction = "Workflow operation : "
						+ endpoint.getQualifier() + " : "
						+ endpoint.getOperationName();
				String payload = "";
				String recipent = "Endpoint: " + endpoint.getEndpointID()
						+ " with address " + endpoint.getEndpointURL();
				String misc = "Invocation "
						+ (success ? "successful" : "failed");

				Session session = sessionFactory.openSession();

				Transaction tx = null;
				// TODO: Das hier in Methode und dann von beiden benutzen...
				try {
					tx = session.beginTransaction();
					HistoryEntry entry = new HistoryEntry(situationString
							.toString(), typeOfAction, recipent, payload, misc);
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
		}).start();
	}

	/**
	 * Gets the history. The entries can be limited by specifying {@code offset}
	 * and {@code numberOfEntries}. The offset is applied backwards. So if there
	 * are n entries in the history, the offset is 10 and numberOfEntries is 50,
	 * the entries n - 10 to n - 60 will be returned, with n -10 as first
	 * element in the list.
	 * 
	 * <p>
	 * If both params are 0, the whole history will be returned.
	 *
	 * @param offset
	 *            the offset the number of the first entry
	 * @param numberOfEntries
	 *            the maximum number of entries to get
	 * @return the history
	 */
	public List<HistoryEntry> getHistory(int offset, int numberOfEntries) {
		if (offset < 0 || numberOfEntries < 0) {
			logger.debug("Illegal usage of get history: Offset: " + offset
					+ " Entries: " + numberOfEntries);
			throw new IllegalArgumentException(
					"Offset and number of entries must be equal or greater than null.");
		}
		logger.debug("Getting " + numberOfEntries
				+ " history entries with offset: " + 0);

		Session session = sessionFactory.openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			@SuppressWarnings("unchecked")
			List<HistoryEntry> entries = session
					.createCriteria(HistoryEntry.class)
					.addOrder(Order.desc("id")).setFirstResult(offset)
					.setMaxResults(numberOfEntries).list();

			tx.commit();
			return entries;
		} catch (JDBCException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Error getting " + numberOfEntries
					+ " history entries from  entry n - " + offset, e);
			return null;
		} finally {
			session.close();
		}
	}

	/**
	 * Gets the total number of entries in the history.
	 * 
	 * @return the history size as long.
	 */
	public long getHistorySize() {
		logger.debug("Getting history size.");

		Session session = sessionFactory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			Number size = (Number) session.createCriteria(HistoryEntry.class)
					.setProjection(Projections.rowCount()).uniqueResult();
			tx.commit();
			return size.longValue();
		} catch (JDBCException e) {
			if (tx != null)
				tx.rollback();
			return -1;
		} finally {
			session.close();
		}
	}

}

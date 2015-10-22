package situationHandling.storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

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
 * <p>
 * The entries are added to the history in an asynchronous way, i.e. adding an
 * entry does not immediately add the entry to the history. If the situation
 * handler fails after calling adding, there is no guarantee that the entries is
 * added to the history.
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
     * Used to run asynchronous tasks
     * 
     */
    private ExecutorService threadExectutor;

    /**
     * Instantiates a new history access.
     *
     * @param sessionFactory
     *            the session factory to access the database
     * @param threadExecutor
     *            used to run asynchronous tasks
     */
    HistoryAccess(SessionFactory sessionFactory, ExecutorService threadExecutor) {
	this.sessionFactory = sessionFactory;
	this.threadExectutor = threadExecutor;
    }

    /**
     * Append an action to the history.
     *
     * @param action
     *            the action to append
     * @param situation
     *            the situation in which the action was used
     * @param state
     *            the state of the situation, i.e. if it occured (true) or not
     *            (false).
     * @param result
     *            the result of the action stored in a map
     */
    public void appendAction(Action action, Situation situation, boolean state,
	    Map<String, String> result) {

	// write history asynchronously in thread
	threadExectutor.submit(new Thread(new Runnable() {
	    @Override
	    public void run() {
		logger.trace("Creating action history entry:" + situation + action);

		String situationString = "Situation defined by SituationTemplate: "
			+ situation.getSituationName() + " and Thing: " + situation.getObjectId()
			+ " has " + (state ? "appeared" : "disappeared");
		String typeOfAction = "Notification with: " + action.getPluginID();

		HistoryEntry entry = new HistoryEntry(situationString, typeOfAction,
			action.getAddress(), action.getPayload(),
			"Used Parameters: " + action.getParams().toString() + "|| Result: "
				+ result.toString());

		storeEntry(entry);
	    }
	}));
    }

    /**
     * Append an workflow operation to the history. This method will create an
     * entry that states that the operation was invoked.
     *
     * @param endpoint
     *            the endpoint that was used to execute the operation
     * @param success
     *            true if the operation was executed successfully, false else
     */
    public void appendWorkflowOperationInvocation(Endpoint endpoint, boolean success) {
	// write history asynchronously in thread
	threadExectutor.submit(new Thread(new Runnable() {

	    @Override
	    public void run() {
		logger.trace("Creating workflow history entry: " + endpoint);

		String misc = "Invocation " + (success ? "successful" : "failed");

		HistoryEntry entry = createWorkflowEntry(endpoint);

		entry.setMisc(misc);

		storeEntry(entry);
	    }
	}));
    }

    /**
     * Append an workflow operation to the history. This method will create an
     * entry that states that the result of an workflow operation was received.
     *
     * @param endpoint
     *            the endpoint that was used to execute the operation
     * @param success
     *            true if the operation was executed successfully, false else
     */
    public void appendWorkflowOperationAnswer(Endpoint endpoint) {
	// write history asynchronously in thread
	threadExectutor.submit(new Thread(new Runnable() {

	    @Override
	    public void run() {
		logger.trace("Creating workflow history entry for answer: " + endpoint);

		String misc = "Answer received. Operation successfully handled";

		HistoryEntry entry = createWorkflowEntry(endpoint);
		entry.setMisc(misc);
		storeEntry(entry);
	    }
	}));
    }

    /**
     * Append an workflow rollback operation to the history. This method will
     * create an entry that states that a situation changed and therefore a
     * rollback was required.
     *
     * @param endpoint
     *            the endpoint that was used to execute the operation
     * @param situation
     *            the situation that triggered the rollback.
     * @param state
     *            the state to which the situation switched.
     */
    public void appendWorkflowRollback(Endpoint endpoint, Situation situation, boolean state) {
	// write history asynchronously in thread
	threadExectutor.submit(new Thread(new Runnable() {

	    @Override
	    public void run() {
		logger.trace("Creating history entry for rollback: " + endpoint);
		HistoryEntry entry = createWorkflowEntry(endpoint);
		String misc = situation.toString() + " changed to " + state
			+ " . Rollback required.";
		entry.setMisc(misc);
		storeEntry(entry);
	    }
	}));
    }

    /**
     * Append an workflow operation rollback answer to the history. This method
     * will create an entry that states that a the rollback of an operation
     * succeeded or not.
     *
     * @param endpoint
     *            the endpoint that was used to execute the operation
     * @param success
     *            true if the rollback was executed successfully, false else
     * @message A message describing the result
     */
    public void appendWorkflowRollbackAnswer(Endpoint endpoint, boolean success, String message) {
	// write history asynchronously in thread
	threadExectutor.submit(new Thread(new Runnable() {

	    @Override
	    public void run() {
		logger.trace("Creating history entry for rollback: " + endpoint);
		HistoryEntry entry = createWorkflowEntry(endpoint);
		String misc = "Rollback " + (success ? "succeeded" : "failed") + " . " + message;
		entry.setMisc(misc);
		storeEntry(entry);
	    }
	}));
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
	    logger.debug("Illegal usage of get history: Offset: " + offset + " Entries: "
		    + numberOfEntries);
	    throw new IllegalArgumentException(
		    "Offset and number of entries must be equal or greater than null.");
	}
	logger.trace("Getting " + numberOfEntries + " history entries with offset: " + 0);

	Session session = sessionFactory.openSession();

	Transaction tx = null;

	try {
	    tx = session.beginTransaction();

	    @SuppressWarnings("unchecked")
	    List<HistoryEntry> entries = session.createCriteria(HistoryEntry.class)
		    .addOrder(Order.desc("id")).setFirstResult(offset)
		    .setMaxResults(numberOfEntries).list();

	    tx.commit();
	    return entries;
	} catch (JDBCException e) {
	    if (tx != null)
		tx.rollback();
	    logger.error("Error getting " + numberOfEntries + " history entries from  entry n - "
		    + offset, e);
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
	logger.trace("Getting history size.");

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

    /**
     * Helper method to store a history entry in the database.
     * 
     * @param entry
     *            the entry to store.
     */
    private void storeEntry(HistoryEntry entry) {
	Session session = sessionFactory.openSession();
	Transaction tx = null;
	try {
	    tx = session.beginTransaction();
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

    /**
     * Helper method to create an entry for workflow operations. Sets all fields
     * except {@code Misc}
     * 
     * @param endpoint
     *            the endpoint that is/was used
     * @return a history entry for this endpoint/operation.
     */
    private HistoryEntry createWorkflowEntry(Endpoint endpoint) {
	StringBuilder situationString = new StringBuilder();
	for (HandledSituation handledSituation : endpoint.getSituations()) {
	    situationString.append(
		    "Situation defined by SituationTemplate: " + handledSituation.getSituationName()
			    + " and Thing: " + handledSituation.getObjectId() + " is "
			    + handledSituation.isSituationHolds());
	}

	String typeOfAction = "Workflow operation : " + endpoint.getQualifier() + " : "
		+ endpoint.getOperationName();
	String payload = "";
	String recipent = "Endpoint: " + endpoint.getEndpointID() + " with address "
		+ endpoint.getEndpointURL();

	return new HistoryEntry(situationString.toString(), typeOfAction, recipent, payload, null);
    }

}

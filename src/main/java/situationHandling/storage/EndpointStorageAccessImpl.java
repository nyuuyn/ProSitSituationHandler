package situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

/**
 * The Class EndpointStorageAccessImpl provides the standard implementation for
 * the {@code Interface} {@link EndpointStorageAccess}. It uses a relational SQL
 * database to store the endpoints. To access the database JPA 2.0/Hibernate is
 * used.
 */
class EndpointStorageAccessImpl implements EndpointStorageAccess {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(EndpointStorageAccessImpl.class);

	/**
	 * Instantiates a new endpoint storage access impl. The default constructor
	 * for this method.
	 */
	EndpointStorageAccessImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.EndpointStorageAccess#getEndpointURL(
	 * situationHandling.storage.datatypes.Situation,
	 * situationHandling.storage.datatypes.Operation)
	 */
	@Override
	public URL getEndpointURL(Situation situation, Operation operation) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		URL endpointurl = null;
		try {
			tx = session.beginTransaction();

			// get all endpoints for the situation and operation
			@SuppressWarnings("rawtypes")
			List endpoints = session.createQuery(
					"SELECT E.endpointURL FROM Endpoint E WHERE E.situationName =  "
							+ addTicks(situation.getSituationName())
							+ " AND E.objectName =  "
							+ addTicks(situation.getObjectName())
							+ " AND E.operationName = "
							+ addTicks(operation.getOperationName())
							+ " AND E.qualifier = "
							+ addTicks(operation.getQualifier())).list();

			if (endpoints.size() == 0) {
				logger.error("No endpoint found for situation!");
			} else {
				// if more than one endpoint was found, return the first one in
				// the list
				endpointurl = new URL((String) endpoints.iterator().next());
				if (endpoints.size() > 1) {
					logger.debug("Found more than one endpoint for situation.");
				}
				logger.debug("Endpoint URL: " + endpointurl.toString());
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} catch (MalformedURLException e) {
			logger.error("Bad Url", e);
		} finally {
			session.close();
		}
		return endpointurl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.EndpointStorageAccess#getAllEndpoints()
	 */
	@Override
	public List<Endpoint> getAllEndpoints() {
		logger.debug("Getting all Endpoints");

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		LinkedList<Endpoint> endpoints = new LinkedList<>();
		try {
			tx = session.beginTransaction();

			@SuppressWarnings("rawtypes")
			List queryResults = session.createQuery("FROM Endpoint").list();

			@SuppressWarnings("rawtypes")
			Iterator it = queryResults.iterator();

			while (it.hasNext()) {
				endpoints.add((Endpoint) it.next());
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		return endpoints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.EndpointStorageAccess#getEndpointByID(int)
	 */
	@Override
	public Endpoint getEndpointByID(int endpointID) {
		logger.debug("Getting endpoint with id " + endpointID);

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		Endpoint endpoint = null;
		try {
			tx = session.beginTransaction();

			endpoint = (Endpoint) session.get(Endpoint.class, endpointID);

			if (endpoint == null) {
				logger.info("No endpoint found with id = " + endpointID);
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		return endpoint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * situationHandling.storage.EndpointStorageAccess#addEndpoint(situationHandling
	 * .storage.datatypes.Operation,
	 * situationHandling.storage.datatypes.Situation, java.net.URL)
	 */
	@Override
	public int addEndpoint(Operation operation, Situation situation,
			URL endpointURL) {

		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		Integer endpointID = null;
		try {
			tx = session.beginTransaction();
			Endpoint endpoint = new Endpoint(endpointURL.toString(), situation,
					operation);
			logger.debug("Adding endpoint " + endpoint.toString());
			endpointID = (Integer) session.save(endpoint);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
		logger.debug("Endpoint added. ID = " + endpointID);
		return endpointID;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see situationHandling.storage.EndpointStorageAccess#deleteEndpoint(int)
	 */
	@Override
	public boolean deleteEndpoint(int endpointID) {
		logger.debug("Deleting endpoint: " + endpointID);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Endpoint endpoint = (Endpoint) session.get(Endpoint.class,
					endpointID);
			if (endpoint == null) {
				logger.info("No Endpoint with id " + endpointID
						+ " found. No endpoint deleted");
				return false;
			} else {
				session.delete(endpoint);
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
	 * @see situationHandling.storage.EndpointStorageAccess#updateEndpoint(int,
	 * situationHandling.storage.datatypes.Situation,
	 * situationHandling.storage.datatypes.Operation, java.net.URL)
	 */
	@Override
	public boolean updateEndpoint(int endpointID, Situation situation,
			Operation operation, URL endpointURL) {

		logger.debug("Updating endpoint: " + endpointID);
		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Endpoint endpoint = (Endpoint) session.get(Endpoint.class,
					endpointID);

			if (endpoint == null) {
				logger.info("No Endpoint with id " + endpointID
						+ " found. No endpoint updated");
				return false;
			} else {

				// params are optional, so check for null..
				if (situation != null) {
					endpoint.setSituation(situation);
				}
				if (operation != null) {
					endpoint.setOperation(operation);
				}
				if (endpointURL != null) {
					endpoint.setEndpointURL(endpointURL.toString());
				}
				session.update(endpoint);
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

	/**
	 * Helper method to wrap a String in {@code '} tokens. Can be used for
	 * database queries to wrap the params.
	 *
	 * @param param
	 *            the String that should be wrapped with ' tokens.
	 * @return the param String wrapped with '. When param is "foo", the method
	 *         returns "'foo'"
	 */
	private String addTicks(String param) {
		return "'" + param + "'";
	}

}

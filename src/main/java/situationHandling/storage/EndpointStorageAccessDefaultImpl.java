package situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

/**
 * The Class EndpointStorageAccessDefaultImpl provides the standard
 * implementation for the {@code Interface} {@link EndpointStorageAccess}. It
 * uses a relational SQL database to store the endpoints. To access the database
 * JPA 2.0/Hibernate is used. <br>
 * The DefaultImpl does only minimal checks on the semantic validity of the
 * inputs and handles errors on database level.
 */
class EndpointStorageAccessDefaultImpl implements EndpointStorageAccess {

	/** The logger for this class. */
	private final static Logger logger = Logger
			.getLogger(EndpointStorageAccessDefaultImpl.class);
	/**
	 * The session factory used to create database sessions.
	 */
	protected SessionFactory sessionFactory;

	/**
	 * Instantiates a new endpoint storage access impl. The default constructor
	 * for this class.
	 * 
	 * @param sessionFactory
	 *            The session factory used to create database sessions.
	 */
	EndpointStorageAccessDefaultImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
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
		Session session = sessionFactory.openSession();

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

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		LinkedList<Endpoint> endpoints = new LinkedList<>();
		try {
			tx = session.beginTransaction();

			@SuppressWarnings("rawtypes")
			List queryResults = session.createQuery("FROM Endpoint").list();

			@SuppressWarnings("rawtypes")
			Iterator it = queryResults.iterator();

			while (it.hasNext()) {
				Endpoint endpoint = (Endpoint) it.next();
				Hibernate.initialize(endpoint.getSituations());
				endpoints.add(endpoint);
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

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Endpoint endpoint = null;
		try {
			tx = session.beginTransaction();

			endpoint = (Endpoint) session.get(Endpoint.class, endpointID);

			if (endpoint == null) {
				logger.info("No endpoint found with id = " + endpointID);
			} else {
				Hibernate.initialize(endpoint.getSituations());
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
	public int addEndpoint(Operation operation,
			List<HandledSituation> situations, String endpointURL)
			throws InvalidEndpointException {

		Session session = sessionFactory.openSession();

		Transaction tx = null;
		Integer endpointID = null;
		try {
			tx = session.beginTransaction();
			Endpoint endpoint = new Endpoint(endpointURL, situations, operation);
			logger.debug("Adding endpoint " + endpoint.toString());
			endpointID = (Integer) session.save(endpoint);
			tx.commit();
		} catch (JDBCException e) {
			if (tx != null)
				tx.rollback();
			throw new InvalidEndpointException(createErrorMessage(e), e);
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
		Session session = sessionFactory.openSession();
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
	public boolean updateEndpoint(int endpointID,
			List<HandledSituation> situations, Operation operation,
			String endpointURL) throws InvalidEndpointException {

		logger.debug("Updating endpoint: " + endpointID);
		Session session = sessionFactory.openSession();
		// TODO: Das ist echt scheiße, wenn man über die API eine Request
		// schickt und dort situationName setzt, anstatt situation.situationName
		// (vor allem beim Update --> hier wird dann der Wert aus
		// situation.situationName übernommen und nicht situationName.)
		// Das gleiche gilt für RULE usw vermutlich auch :(
		// Es ist eigentlich Aufgabe der API zu checken, dass da was valides
		// reinkommt!
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

				if (operation != null && operation.getOperationName() != null) {
					endpoint.setOperationName(operation.getOperationName());
				}
				if (operation != null && operation.getQualifier() != null) {
					endpoint.setQualifier(operation.getQualifier());
				}
				if (endpointURL != null) {
					endpoint.setEndpointURL(endpointURL);
				}
				session.update(endpoint);
			}
			tx.commit();
			// update situations manually (avoids hibernate messing everything up)
			if (situations != null) {
				for (HandledSituation handledSituation: situations){
					updateHandledSituation(
							handledSituation.getId(), handledSituation);
				}
			}
		} catch (JDBCException e ) {
			if (tx != null)
				tx.rollback();
			throw new InvalidEndpointException(createErrorMessage(e));
		} finally {
			session.close();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see situationHandling.storage.EndpointStorageAccess#updateHandledSituation(int, situationHandling.storage.datatypes.HandledSituation)
	 */
	@Override
	public boolean updateHandledSituation(int id, HandledSituation newSituation)
			throws InvalidEndpointException {
		logger.debug("Updating Situation: " + id);
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			HandledSituation existingSituation = (HandledSituation) session
					.get(HandledSituation.class, id);

			if (existingSituation == null) {
				logger.info("No Situation with id " + id
						+ " found. Situation endpoint updated");
				return false;
			} else {
				//check for props to update and do update if check successful
				if (newSituation.getObjectName() != null) {
					existingSituation.setObjectName(newSituation
							.getObjectName());
				}
				if (newSituation.getSituationName() != null) {
					existingSituation.setSituationName(newSituation
							.getSituationName());
				}
				if (newSituation.isSituationHolds() != null) {
					existingSituation.setSituationHolds(newSituation
							.isSituationHolds());
				}
				if (newSituation.isOptional() != null) {
					existingSituation.setOptional(newSituation.isOptional());
				}
				if (newSituation.isRollbackOnChange() != null) {
					existingSituation.setRollbackOnChange(newSituation
							.isRollbackOnChange());
				}
				session.update(existingSituation);

			}
			tx.commit();
		} catch (JDBCException e) {
			if (tx != null)
				tx.rollback();
			throw new InvalidEndpointException(createErrorMessage(e));
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

	/**
	 * Conience Method to create error messages for JDBC Exceptions
	 * 
	 * @param e
	 *            the exception
	 * @return A nicely readable error message.
	 */
	private String createErrorMessage(JDBCException e) {
		String errorMessage;
		if (e.getErrorCode() == 1048) {// column not set
			errorMessage = "Endpoint property not set. Please set all properties.";
		} else if (e.getErrorCode() == 1062) {// duplicate
			errorMessage = "Duplicate endpoint. There exists already an identical endpoint.";
		} else {// unknown
			errorMessage = "Unknown error when creating endpoint.";
		}
		logger.debug(errorMessage);
		return errorMessage;
	}

}

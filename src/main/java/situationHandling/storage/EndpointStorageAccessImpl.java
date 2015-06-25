package situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

class EndpointStorageAccessImpl implements EndpointStorageAccess {

	private final static Logger logger = Logger
			.getLogger(EndpointStorageAccessImpl.class);

	EndpointStorageAccessImpl() {
	}

	@Override
	public URL getEndpointURL(Situation situation, Operation operation) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		URL endpointurl = null;
		try {
			tx = session.beginTransaction();

			@SuppressWarnings("rawtypes")


			List endpoints = session.createQuery(
					"SELECT E.endpointURL FROM Endpoint E WHERE E.situationName =  "
							+addTicks(situation.getSituationName())
							+ " AND E.objectName =  "
							+ addTicks(situation.getObjectName())
							+ " AND E.operationName = "
							+ addTicks(operation.getOperationName())
							+ " AND E.qualifier = "
							+ addTicks(operation.getQualifier())).list();

			if (endpoints.size() == 0) {
				logger.error("No endpoint found for situation!");
			} else {
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
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return endpointurl;
	}

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
			e.printStackTrace();
		} finally {
			session.close();
		}
		return endpoints;
	}

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
			e.printStackTrace();
		} finally {
			session.close();
		}
		logger.debug("Endpoint added. ID = " + endpointID);
		return endpointID;

	}

	@Override
	public boolean deleteEndpoint(int endpointID) {
		logger.debug("Deleting endpoint: " + endpointID);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Endpoint endpoint = (Endpoint) session.get(Endpoint.class,
					endpointID);
			session.delete(endpoint);
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
	public boolean updateEndpoint(int endpointID, Situation situation,
			Operation operation, URL endpointURL) {
		
		logger.debug("Updating endpoint: " + endpointID);
		Session session = HibernateUtil.getSessionFactory().openSession();

		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Endpoint endpoint = (Endpoint) session.get(Endpoint.class,
					endpointID);
			if (situation != null){
				endpoint.setSituation(situation);
				
			}
			if(operation != null){
				endpoint.setOperation(operation);
			}
			if (endpointURL != null){
				endpoint.setEndpointURL(endpointURL.toString());
			}
			session.update(endpoint);
			
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

	private String addTicks(String param) {
		return "'" + param + "'";
	}

}

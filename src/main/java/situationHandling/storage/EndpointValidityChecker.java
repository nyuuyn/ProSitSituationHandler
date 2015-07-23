package situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

class EndpointValidityChecker {

	private SessionFactory sessionFactory;
	private Situation situation;
	private Operation operation;
	private String endpointUrl;
	private int Id;

	private static final Logger logger = Logger
			.getLogger(EndpointValidityChecker.class);

	EndpointValidityChecker(SessionFactory sessionFactory, Situation situation,
			Operation operation, String endpointURL, int Id) {
		this.sessionFactory = sessionFactory;
		this.situation = situation;
		this.operation = operation;
		this.endpointUrl = endpointURL;
		this.Id = Id;
	}

	private void nullChecks() throws InvalidEndpointException {
		if (situation == null) {
			throw new InvalidEndpointException("No Situation specified");
		} else {
			if (situation.getSituationName() == null) {
				throw new InvalidEndpointException("No situation name specified");
			} else if (situation.getObjectName() == null) {
				throw new InvalidEndpointException("No object name specified");
			}
		}
		if (operation == null) {
			throw new InvalidEndpointException("No Operation specified");
		} else {
			if (operation.getOperationName() == null) {
				throw new InvalidEndpointException("No Operationname specified");
			} else if (operation.getQualifier() == null) {
				throw new InvalidEndpointException("No Operation Qualifier specified");
			}
		}
	}

	private void checkDoubleOccurence() throws InvalidEndpointException {
		Session session = sessionFactory.openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			
			Map<String, String> endpointProperties = new HashMap<>();
			endpointProperties.put("situationName", situation.getSituationName());
			endpointProperties.put("objectName", situation.getObjectName());
			endpointProperties.put("operationName", operation.getOperationName());
			endpointProperties.put("qualifier", operation.getQualifier());
			endpointProperties.put("endpointURL", endpointUrl);
			
			@SuppressWarnings("unchecked")
			List<Endpoint> endpoints = session.createCriteria(Endpoint.class).add(Restrictions.allEq(endpointProperties )).list();

			if (endpoints.size() > 0){
				logger.debug("Endpoint Duplicate " + endpoints.get(0).toString());
				throw new InvalidEndpointException("This endpoint already exists.");
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
	}
	
	private void loadEndpoint() throws InvalidEndpointException{
		Session session = sessionFactory.openSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			
			Endpoint endpoint = (Endpoint) session.get(Endpoint.class, Id);

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			logger.error("Hibernate error", e);
		} finally {
			session.close();
		}
	}

	private void checkUrl() throws InvalidEndpointException {
		try {
			new URL(endpointUrl);
		} catch (MalformedURLException e) {
			throw new InvalidEndpointException("Invalid Endpoint URL", e);
		}
	}

	public void checkBeforeAdd() throws InvalidEndpointException {
		nullChecks();
		checkUrl();
		checkDoubleOccurence();
	}

	public void checkBeforeUpdate() throws InvalidEndpointException {
		if (endpointUrl != null) {
			checkUrl();
		}
		
		

	}

}

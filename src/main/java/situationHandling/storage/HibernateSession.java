package situationHandling.storage;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.HistoryEntry;
import situationHandling.storage.datatypes.Rule;

/**
 * The Class HibernateSession initiates the configuration of hibernate and gives
 * access to the factory that is required to create database sessions. A
 * database session is needed for each interaction with the database using
 * hibernate.See the hibernate documentation. <br>
 * Since instances of this class are rather heavyweight, it is recommended not
 * to create many instances (or better only one) and use the {@code shutdown}
 * operation to release all resources after you are finished.
 * 
 */
public class HibernateSession {

	/**
	 * The session factory. The hibernate SessionFactory is a heavyweight
	 * object. Therefore only one instance of this factory should exist.
	 */
	private SessionFactory factory;

	/** The service registry that is used with the session factory. */
	private ServiceRegistry serviceRegistry;

	/** The logger. */
	private final static Logger logger = Logger
			.getLogger(HibernateSession.class);

	/**
	 * Create a new instance of HibernateSession. Does all the necessary
	 * initializiation. When you are finished using this object, use
	 * {@code shutdown} to release all ressource!.
	 * 
	 */
	HibernateSession() {
		/*
		 * Initiate Hibernate, i.e. create isntances of Session Factory and
		 * ServiceRegistry.
		 */
		Configuration configuration = new Configuration();
		configuration.configure();

		// adding annotated classes to configuration
		configuration.addAnnotatedClass(Endpoint.class);
		configuration.addAnnotatedClass(Rule.class);
		configuration.addAnnotatedClass(Action.class);
		configuration.addAnnotatedClass(HandledSituation.class);
		configuration.addAnnotatedClass(HistoryEntry.class);

		serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
				configuration.getProperties()).build();

		try {
			factory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) {
			logger.error("Failed to create sessionFactory object.", ex);
			throw new ExceptionInInitializerError(ex);
		}

		logger.info("Hibernate session factory started.");
	}

	/**
	 * Gets the session factory. The session factory can be used to create
	 * sessions to interact with the database.
	 * 
	 * @return the session factory
	 */
	SessionFactory getSessionFactory() {
		return factory;
	}

	/**
	 * Shuts down the Hibernate Session and releases all occupied ressources. It
	 * is recommended to use this method when there is no further access to the
	 * storage required. <br>
	 * Note that a HibernateSession cannot be reopened, so use this method with
	 * care. However, to create a new session just create a new isntance of
	 * {@code HibernateSession}. It is not recommended to use more than one
	 * instance of {@code HibernateSession} at once!
	 * 
	 */
	void shutdown() {
		factory.close();
		// http://stackoverflow.com/questions/21645516/program-using-hibernate-does-not-terminate
		StandardServiceRegistryBuilder.destroy(serviceRegistry);
	}

}

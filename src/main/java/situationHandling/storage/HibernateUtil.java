package situationHandling.storage;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Rule;

/**
 * The Class HibernateUtil initiates the configuration of hibernate and gives
 * access to the factory that is required to create hibernate sessions. A
 * Hibernate session is needed for each interaction with the database using
 * hibernate.See the hibernate documentation.
 */
public class HibernateUtil {

	// TODO: die Service Registry sollte man beim shutdown wohl irgendwie
	// zerstören
	// http://stackoverflow.com/questions/21645516/program-using-hibernate-does-not-terminate
	/**
	 * The session factory. The hibernate SessionFactory is a heavyweight
	 * object. Therefore only one instance of this factory should exist.
	 */
	// TODO: Das hier wegschmeissen und stattdessen Spring benutzen?
	private static SessionFactory factory;

	/** The service registry that is used with the session factory. */
	private static ServiceRegistry serviceRegistry;

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(HibernateUtil.class);

	static {
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
	 *
	 * @return the session factory
	 */
	public static SessionFactory getSessionFactory() {
		return factory;
	}
	
}

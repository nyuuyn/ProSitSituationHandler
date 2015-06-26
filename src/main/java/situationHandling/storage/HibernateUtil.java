package situationHandling.storage;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Rule;

class HibernateUtil {

	// TODO: die Service Registry sollte man beim shutdown wohl irgendwie
	// zerstören
	// http://stackoverflow.com/questions/21645516/program-using-hibernate-does-not-terminate
	// TODO: Das hier wegschmeissen und stattdessen Spring benutzen?
	private static SessionFactory factory;
	private static ServiceRegistry serviceRegistry;
	private final static Logger logger = Logger.getLogger(HibernateUtil.class);

	static {

		Configuration configuration = new Configuration();
		configuration.configure();
		configuration.addAnnotatedClass(Endpoint.class);
		configuration.addAnnotatedClass(Rule.class);
		configuration.addAnnotatedClass(Action.class);

		serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
				configuration.getProperties()).build();

		try {
			factory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

		logger.info("Hibernate session factory started.");
	}

	static SessionFactory getSessionFactory() {
		return factory;
	}

}

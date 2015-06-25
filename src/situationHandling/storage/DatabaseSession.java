package situationHandling.storage;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

class DatabaseSession {

	static SessionFactory factory;
	private static ServiceRegistry serviceRegistry;
	
	static{
		
		Configuration configuration = new Configuration();
		configuration.configure();
		
		serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
	            configuration.getProperties()).build();
		
	      try{
	          factory =  configuration.buildSessionFactory(serviceRegistry);
	       }catch (Throwable ex) { 
	          System.err.println("Failed to create sessionFactory object." + ex);
	          throw new ExceptionInInitializerError(ex); 
	       }
	}

}

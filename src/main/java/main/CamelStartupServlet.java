package main;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class CamelStartupServlet implements ServletContextListener {

    private final static Logger logger = Logger.getLogger(CamelStartupServlet.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
	logger.info("Situation Handler Startup");
	SituationHandlerInitializer.startInServletContainer(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
	SituationHandlerInitializer.shutdown();
    }

}

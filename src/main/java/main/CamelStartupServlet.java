package main;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * The CamelStartupServlet does the startup and shutdown of the situation
 * handler, when the handler is started on a server.
 * 
 * @author Stefan
 *
 */
public class CamelStartupServlet implements ServletContextListener {

    /** The logger. */
    private final static Logger logger = Logger.getLogger(CamelStartupServlet.class);


    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
	logger.info("Starting situation handler on server.");
	SituationHandlerInitializer.startInServletContainer(sce);
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
	SituationHandlerInitializer.shutdown();
    }

}

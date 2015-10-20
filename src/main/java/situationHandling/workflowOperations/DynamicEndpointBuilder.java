package situationHandling.workflowOperations;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * The clss {@link DynamicEndpointBuilder} is used to dynamically create http
 * endpoints at runtime using Apache camel. The endpoints can be used to receive
 * {@link DecisionAnswer}. The processor used on the route processes messages
 * and should make a callback to the thing waiting for the answer.
 * 
 * @author Stefan
 *
 */
public class DynamicEndpointBuilder extends RouteBuilder {

    /**
     * The uri of the http endpoint. See Apache Camel Doc for more information.
     */
    private final String from;

    /**
     * The processor the processes messages posted on this http endpoint.
     */
    private final Processor resultHandler;

    /**
     * The id to use for the newly created route.
     */
    private final String routeId;

    /**
     * 
     * Creates a new instance of {@link DynamicEndpointBuilder}. Automatically
     * creates the http endpoint, i.e. the new camel route.
     * 
     * @param context
     *            the camel context the new route should run in.
     * @param from
     *            The uri of the http endpoint. See Apache Camel Doc for more
     *            information.
     * @param resultHandler
     *            The processor the processes messages posted on this http
     *            endpoint.
     * @param routeId
     *            The id to use for the newly created route.
     */
    public DynamicEndpointBuilder(CamelContext context, String from, Processor resultHandler,
	    String routeId) {
	super(context);
	this.from = from;
	this.resultHandler = resultHandler;
	this.routeId = routeId;
    }

    @Override
    public void configure() throws Exception {
	from(from).routeId(routeId).process(resultHandler);
    }

}

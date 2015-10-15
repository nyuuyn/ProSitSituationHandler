package situationHandling.workflowOperations;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class DynamicEndpointBuilder extends RouteBuilder {

    private final String from;
    private final Processor resultHandler;
    private final String routeId;

    public DynamicEndpointBuilder(CamelContext context, String from, Processor resultHandler, String routeId) {
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

package restApiImpl;

import java.security.InvalidParameterException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.commons.io.IOUtils;

import main.SituationHandlerProperties;
import pluginManagement.PluginInfo;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.HandledSituation;
import situationHandling.storage.datatypes.HistoryEntry;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

/**
 * In this class, the camel routes for the rest configuration api are designed,
 * i.e. the rest operations for the rest api are defined.
 * <p>
 * Extends Route Builder. To use the class, i.e. to make the rest api available,
 * add this class as route to the camel context.
 * 
 * @author Stefan
 *
 */
public class RestApiRoutes extends RouteBuilder {

    /**
     * The base path that defines the component and the address (if necessary)
     * 
     */
    private final String path;

    /**
     * The camel component that is used for the rest routes.
     * 
     */
    private String component;

    private final String restApiBasePath;

    /**
     * Contains the swagger.json loaded from the file system.
     * 
     */
    private Object swaggerDoc;

    /**
     * Creates a new instance of RestApiRoutes and does the initialization. Add
     * an instance of this class to the camel context to make the routes
     * available.
     * 
     * @param component
     *            The camel component that is used for the rest routes.
     */
    public RestApiRoutes(String component) {
	if (component.equals("jetty")) {
	    path = "jetty:http://0.0.0.0:" + SituationHandlerProperties.getNetworkPort();
	    restApiBasePath = SituationHandlerProperties.getRestBasePath() + "/"
		    + SituationHandlerProperties.getRestApiBasePath();
	} else if (component.equals("servlet")) {
	    path = "servlet://";
	    restApiBasePath = SituationHandlerProperties.getRestApiBasePath();
	} else {
	    throw new IllegalArgumentException("Unsupported Component: " + component);
	}
	System.out.println("Restapibasepath " + restApiBasePath);
	this.component = component;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.builder.RouteBuilder#configure()
     */
    @Override
    public void configure() throws Exception {

	restSetup();
	createEndpointApi();
	createRuleApi();
	createPluginApi();
	createHistoryApi();

	provideDocumentation();
    }

    /**
     * Basic setup of the rest routes
     */
    private void restSetup() {

	// set Cors,max file size and rest Configuration. headers differ for the
	if (component.equals("jetty")) {
	    // set CORS Headers for option requests and max file size
	    from(path + "/" + restApiBasePath
		    + "?matchOnUriPrefix=true&httpMethodRestrict=OPTIONS&chunkedMaxContentLength="
		    + SituationHandlerProperties.getMaximumFilesize())
			    .setHeader("Access-Control-Allow-Origin").constant("*")
			    .setHeader("Access-Control-Allow-Methods")
			    .constant(
				    "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH")
			    .setHeader("Access-Control-Allow-Headers").constant(
				    "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, x-file-last-modified,x-file-name,x-file-size");

	    // setup configuration for rest routes and max file size
	    restConfiguration().component(component)
		    .port(SituationHandlerProperties.getNetworkPort()).host("0.0.0.0")
		    .bindingMode(RestBindingMode.json).dataFormatProperty("prettyPrint", "true")
		    .enableCORS(true).componentProperty("chunkedMaxContentLength",
			    String.valueOf(SituationHandlerProperties.getMaximumFilesize()));

	} else if (component.equals("servlet")) {
	    from(path + "/" + restApiBasePath + "?matchOnUriPrefix=true&httpMethodRestrict=OPTIONS")
		    .setHeader("Access-Control-Allow-Origin").constant("*")
		    .setHeader("Access-Control-Allow-Methods")
		    .constant("GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH")
		    .setHeader("Access-Control-Allow-Headers").constant(
			    "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, x-file-last-modified,x-file-name,x-file-size");

	    // setup configuration for rest routes and max file size
	    restConfiguration().component(component).bindingMode(RestBindingMode.json)
		    .dataFormatProperty("prettyPrint", "true").enableCORS(true);

	} else {
	    throw new InvalidParameterException("Unsupported Component: " + component);
	}

	// base route
	rest(restApiBasePath).consumes("application/json").produces("application/json")
		.enableCORS(true);
    }

    /**
     * sets up the routes for the endpoint Api
     */
    private void createEndpointApi() {
	// ../endpoints --> GET all endpoints
	rest(restApiBasePath + "/endpoints").get().outTypeList(Endpoint.class)
		.to("bean:endpointApi?method=getEndpoints");

	// ../endpoints --> Post: add new endpoint
	rest(restApiBasePath + "/endpoints").post().type(Endpoint.class)
		.to("bean:endpointApi?method=addEndpoint");

	// ../endpoints/<id> --> GET: gets the endpoint with <id>
	rest(restApiBasePath + "/endpoints/{endpointId}").get().outType(Endpoint.class)
		.to("bean:endpointApi?method=getEndpointByID(${header.endpointId})");

	// ../endpoints/<id> --> DELETE: deletes the endpoint with <id>
	rest(restApiBasePath + "/endpoints/{endpointId}").delete()
		.to("bean:endpointApi?method=deleteEndpoint(${header.endpointId})");

	// ../endpoints/<id> --> PUT: updates the endpoint with <id>
	rest(restApiBasePath + "/endpoints/{endpointId}").put().type(Endpoint.class)
		.to("bean:endpointApi?method=updateEndpoint(${header.endpointId})");

	// ../endpoints/<id>/handledSituations -->GET: get all handled
	// situations
	rest(restApiBasePath + "/endpoints/{endpointId}/handledSituations").get()
		.outTypeList(HandledSituation.class)
		.to("bean:endpointApi?method=getAllHandledSituations(${header.endpointId})");

	// ../endpoints/<id>/handledSituations -->POST: add handled situation
	rest(restApiBasePath + "/endpoints/{endpointId}/handledSituations").post()
		.type(HandledSituation.class)
		.to("bean:endpointApi?method=addHandledSituation(${header.endpointId})");
	// ../endpoints/<id>/handledSituations/<id> -->GET: get handled
	// situation
	rest(restApiBasePath + "/endpoints/{endpointId}/handledSituations/{situationId}").get()
		.outType(HandledSituation.class)
		.to("bean:endpointApi?method=getHandledSituation(${header.situationId})");
	// ../endpoints/<id>/handledSituations/<id> -->DELETE: delete handled
	// situation
	rest(restApiBasePath + "/endpoints/{endpointId}/handledSituations/{situationId}").delete()
		.to("bean:endpointApi?method=deleteHandledSituation(${header.situationId})");
	// ../endpoints/<id>/handledSituations/<id> -->PUT: update handled
	// situation
	rest(restApiBasePath + "/endpoints/{endpointId}/handledSituations/{situationId}").put()
		.type(HandledSituation.class)
		.to("bean:endpointApi?method=updateHandledSituation(${header.situationId})");

    }

    /**
     * sets up the routes for the rule Api
     */
    private void createRuleApi() {
	// ../rules --> GET all rules
	rest(restApiBasePath + "/rules").get().outTypeList(Rule.class)
		.to("bean:ruleApi?method=getRules");

	// ../rules -->POST: creates a new rule
	rest(restApiBasePath + "/rules").post().type(Rule.class).to("bean:ruleApi?method=addRule");

	// ../rules/<ID> -->GET rules with <ID>
	rest(restApiBasePath + "/rules/{ruleId}").get().outType(Rule.class)
		.to("bean:ruleApi?method=getRuleByID(${header.ruleId})");

	// ../rules/<ID> -->PUT: updates the rule with this id
	rest(restApiBasePath + "/rules/{ruleId}").put().type(Situation.class)
		.to("bean:ruleApi?method=updateRuleSituation(${header.ruleId})");

	// ../rules/<ID> -->Delete: deletes the rule with this id
	rest(restApiBasePath + "/rules/{ruleId}").delete()
		.to("bean:ruleApi?method=deleteRule(${header.ruleId})");

	// ../rules/<ID>/actions --> GET all actions of rule <ID>
	rest(restApiBasePath + "/rules/{ruleId}/actions").get().outTypeList(Action.class)
		.to("bean:ruleApi?method=getActionsByRule(${header.ruleId})");

	// ../rules/<ID>/actions -->POST: creates a new action
	rest(restApiBasePath + "/rules/{ruleId}/actions").post().type(Action.class)
		.to("bean:ruleApi?method=addAction(${header.ruleId})");

	// ../rules/<ID>/actions/<actionID> --> GET action with <actionID>
	rest(restApiBasePath + "/rules/{ruleId}/actions/{actionId}").get().outType(Action.class)
		.to("bean:ruleApi?method=getActionByID(${header.actionId})");

	// ../rules/<ID>/actions/<actionID> --> PUT: updates the action with
	// <actionID>
	rest(restApiBasePath + "/rules/{ruleId}/actions/{actionId}").put().type(Action.class)
		.to("bean:ruleApi?method=updateAction(${header.actionId})");

	// ../rules/<ID>/actions/<actionID> --> DELETE: deltes the action with
	// <actionID>
	rest(restApiBasePath + "/rules/{ruleId}/actions/{actionId}").delete()
		.to("bean:ruleApi?method=deleteAction(${header.actionId})");

    }

    /**
     * sets up the routes for the plugin Api
     */
    private void createPluginApi() {
	// ../plugins --> GET information about all plugins
	rest(restApiBasePath + "/plugins").get().outTypeList(PluginInfo.class)
		.to("bean:pluginApi?method=getPlugins");

	// ../plugins --> POST: add a new Plugin
	rest(restApiBasePath + "/plugins").post().consumes("multipart/form-data")
		.bindingMode(RestBindingMode.off).to("bean:pluginApi?method=addPlugin");

	// ../plugins/<ID> --> GET information about the plugin with <ID>
	rest(restApiBasePath + "/plugins/{pluginID}").get().outType(PluginInfo.class)
		.to("bean:pluginApi?method=getPluginByID(${header.pluginID})");

	// ../plugins/<ID>/manual --> GET the manual of the plugin with <ID>
	rest(restApiBasePath + "/plugins/{pluginID}/manual").get()
		.to("bean:pluginApi?method=getPluginManual(${header.pluginID})");

	// ../plugins/<id> --> DELETE: deletes the plugin with <id>
	rest(restApiBasePath + "/plugins/{pluginID}").delete()
		.to("bean:pluginApi?method=deletePlugin(${header.pluginID}, ${header.delete})");

    }

    /**
     * sets up the routes for the history Api
     */
    private void createHistoryApi() {
	rest(restApiBasePath + "/history").get().outTypeList(HistoryEntry.class)
		.to("bean:historyApi?method=getHistory");

    }

    /**
     * provides the documentation (as swagger file) for the rest api
     */
    private void provideDocumentation() {
	from(path + "/" + restApiBasePath + "/swagger.json").process(new Processor() {

	    @Override
	    public void process(Exchange exchange) throws Exception {
		if (swaggerDoc == null) {
		    swaggerDoc = IOUtils.toString(
			    this.getClass().getClassLoader().getResourceAsStream("swagger.json"));
		}
		exchange.getIn().removeHeader("Access-Control-Allow-Origin");
		exchange.getIn().setBody(swaggerDoc);
		exchange.getIn().setHeader("Content-Type", "application/json;charset=UTF-8");
		exchange.getIn().setHeader("connection", "close");
		exchange.getIn().setHeader("Access-Control-Allow-Headers",
			"Content-Type, api_key, Authorization, Origin");
		exchange.getIn().setHeader("Access-Control-Allow-Origin", "*");
		exchange.getIn().setHeader("Access-Control-Allow-Methods",
			"GET, POST, DELETE, PUT");
	    }
	});
    }
}

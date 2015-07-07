package routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import pluginManagement.PluginInfo;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Endpoint;
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
class RestApiRoutes extends RouteBuilder {

	/**
	 * The port under which the api is available
	 */
	private int port;

	/**
	 * The hostname/IP-Adress
	 */
	private String host;

	/**
	 * Creates a new instance of RestApiRoutes and does the initialization. Add
	 * an instance of this class to the camel context to make the routes
	 * available under the given hostname and port.
	 * 
	 * 
	 * @param host
	 *            the hostname, for example "localhost". Use "0.0.0.0" to expose
	 *            the routes on all interfaces.
	 * @param port
	 *            the port
	 */
	public RestApiRoutes(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.builder.RouteBuilder#configure()
	 */
	@Override
	public void configure() throws Exception {

		// TODO: Workaround. Fix in Camel 15.3, siehe Link in Dropbox. Sollte
		// dann vllt auch mit Jetty oder sonstigem wieder gehn
		// set CORS Headers for option requests
		from(
				"netty4-http:http://" + host + ":" + port
						+ "?matchOnUriPrefix=true&httpMethodRestrict=OPTIONS")
				.setHeader("Access-Control-Allow-Origin")
				.constant("*")
				.setHeader("Access-Control-Allow-Methods")
				.constant(
						"GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH")
				.setHeader("Access-Control-Allow-Headers")
				.constant(
						"Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");

		// setup configuration
		restConfiguration().component("netty4-http").port(port).host(host)
				.bindingMode(RestBindingMode.json)
				.dataFormatProperty("prettyPrint", "true").enableCORS(true);

		// base route
		// TODO: Was ist mit den Consumes/Produces dinger?
		rest("/config").description("Situation Handler RestAPI")
				.consumes("application/json").produces("application/json");

		/*
		 * --------------------------------------------------------------------
		 * RULES configuration
		 * --------------------------------------------------------------------
		 */
		// Rest Api operations
		// ../rules --> GET all rules
		rest("/config/rules").get().outTypeList(Rule.class)
				.to("bean:ruleApi?method=getRules");

		// ../rules -->POST: creates a new rule
		rest("/config/rules").post().type(Rule.class)
				.to("bean:ruleApi?method=addRule");

		// ../rules/<ID> -->GET rules with <ID>
		rest("config/rules/{ruleId}").get().outType(Rule.class)
				.to("bean:ruleApi?method=getRuleByID(${header.ruleId})");

		// ../rules/<ID> -->PUT: updates the rule with this id
		rest("config/rules/{ruleId}")
				.put()
				.type(Situation.class)
				.to("bean:ruleApi?method=updateRuleSituation(${header.ruleId})");

		// ../rules/<ID> -->Delete: deletes the rule with this id
		rest("config/rules/{ruleId}").delete().to(
				"bean:ruleApi?method=deleteRule(${header.ruleId})");

		// ../rules/<ID>/actions --> GET all actions of rule <ID>
		rest("config/rules/{ruleId}/actions").get().outTypeList(Action.class)
				.to("bean:ruleApi?method=getActionsByRule(${header.ruleId})");

		// ../rules/<ID>/actions -->POST: creates a new action
		rest("/config/rules/{ruleId}/actions").post().type(Action.class)
				.to("bean:ruleApi?method=addAction(${header.ruleId})");

		// ../rules/<ID>/actions/<actionID> --> GET action with <actionID>
		rest("config/rules/{ruleId}/actions/{actionId}").get()
				.outType(Action.class)
				.to("bean:ruleApi?method=getActionByID(${header.actionId})");

		// ../rules/<ID>/actions/<actionID> --> PUT: updates the action with
		// <actionID>
		rest("config/rules/{ruleId}/actions/{actionId}").put()
				.type(Action.class)
				.to("bean:ruleApi?method=updateAction(${header.actionId})");

		// ../rules/<ID>/actions/<actionID> --> DELETE: deltes the action with
		// <actionID>
		rest("config/rules/{ruleId}/actions/{actionId}").delete().to(
				"bean:ruleApi?method=deleteAction(${header.actionId})");

		/*
		 * --------------------------------------------------------------------
		 * ENDPOINTS configuration
		 * --------------------------------------------------------------------
		 */

		// ../endpoints --> GET all endpoints
		rest("/config/endpoints").get().outTypeList(Endpoint.class)
				.to("bean:endpointApi?method=getEndpoints");

		// ../endpoints --> Post: add new endpoint
		rest("/config/endpoints").post().type(Endpoint.class)
				.to("bean:endpointApi?method=addEndpoint");

		// ../endpoints/<id> --> GET: gets the endpoint with <id>
		rest("/config/endpoints/{endpointId}")
				.get()
				.outType(Endpoint.class)
				.to("bean:endpointApi?method=getEndpointByID(${header.endpointId})");

		// ../endpoints/<id> --> DELETE: deletes the endpoint with <id>
		rest("/config/endpoints/{endpointId}").delete().to(
				"bean:endpointApi?method=deleteEndpoint(${header.endpointId})");

		// ../endpoints/<id> --> PUT: updates the endpoint with <id>
		rest("/config/endpoints/{endpointId}")
				.put()
				.type(Endpoint.class)
				.to("bean:endpointApi?method=updateEndpoint(${header.endpointId})");

		/*
		 * --------------------------------------------------------------------
		 * Plugins configuration
		 * --------------------------------------------------------------------
		 */

		// ../plugins --> GET information about all plugins
		rest("/config/plugins").get().outTypeList(PluginInfo.class)
				.to("bean:pluginAPI?method=getPlugins");

		// ../endpoints --> POST: add a new Plugin
		// TODO:Hier ist noch ein gescheiter Typ n�tig, au�erdem muss hier
		// irgendwie eine Datei akzeptiert werden
		rest("/config/plugins").post().type(Object.class)
				.to("bean:pluginAPI?method=addPlugin");

		// ../plugins/<ID> --> GET information about the plugin with <ID>
		rest("/config/plugins/{pluginID}").get().outType(PluginInfo.class)
				.to("bean:pluginAPI?method=getPluginByID(${header.pluginID})");
		
		// ../plugins/<id> --> DELETE: deletes the plugin with <id>
		rest("/config/plugins/{pluginID}").delete().to(
				"bean:endpointApi?method=deletePlugin(${header.pluginID})");

	}
}

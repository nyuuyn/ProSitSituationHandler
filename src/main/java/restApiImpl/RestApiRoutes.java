package restApiImpl;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.commons.io.IOUtils;

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
	 * The port under which the api is available
	 */
	private int port;

	/**
	 * The hostname/IP-Adress
	 */
	private String host;

	/**
	 * the maximum file size that is allowed for post operations in Bytes
	 * (especially when submitting files)
	 */
	private int maxFileSize;

	/**
	 * The camel component that is used for the rest routes.
	 * 
	 */
	private String component;

	/**
	 * Contains the swagger.json loaded from the file system.
	 * 
	 */
	private Object swaggerDoc;

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
	 * 
	 * @param maxFileSize
	 *            the maximum file size that is allowed for post operations in
	 *            Bytes (especially when submitting files)
	 * @param component
	 *            The camel component that is used for the rest routes.
	 */
	public RestApiRoutes(String host, int port, int maxFileSize,
			String component) {
		this.host = host;
		this.port = port;
		this.maxFileSize = maxFileSize;
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

	//TODO: Comments!
	private void restSetup() {
		// TODO: Workaround. Fix in Camel 15.3, siehe Link in Dropbox. Sollte
		// dann vllt auch mit Jetty wieder gehn
		// set CORS Headers for option requests and max file size
		from(
				component
						+ ":http://"
						+ host
						+ ":"
						+ port
						+ "/config?matchOnUriPrefix=true&httpMethodRestrict=OPTIONS&chunkedMaxContentLength="
						+ maxFileSize)
				.setHeader("Access-Control-Allow-Origin")
				.constant("*")
				.setHeader("Access-Control-Allow-Methods")
				.constant(
						"GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH")
				.setHeader("Access-Control-Allow-Headers")
				.constant(
						"Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, x-file-last-modified,x-file-name,x-file-size");

		// setup configuration for rest routes and max file size
		restConfiguration()
				.component(component)
				.port(port)
				.host(host)
				.bindingMode(RestBindingMode.json)
				.dataFormatProperty("prettyPrint", "true")
				.enableCORS(true)
				.componentProperty("chunkedMaxContentLength",
						String.valueOf(maxFileSize));

		// base route
		// TODO: Was ist mit den Consumes/Produces dinger?
		rest("/config").description("Situation Handler RestAPI")
				.consumes("application/json").produces("application/json");

	}

	private void createEndpointApi() {
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

		// ../endpoints/<id>/handledSituations -->GET: get all handled
		// situations
		rest("/config/endpoints/{endpointId}/handledSituations")
				.get()
				.outTypeList(HandledSituation.class)
				.to("bean:endpointApi?method=getAllHandledSituations(${header.endpointId})");

		// ../endpoints/<id>/handledSituations -->POST: add handled situation
		rest("/config/endpoints/{endpointId}/handledSituations")
				.post()
				.type(HandledSituation.class)
				.to("bean:endpointApi?method=addHandledSituation(${header.endpointId})");
		// ../endpoints/<id>/handledSituations/<id> -->GET: get handled
		// situation
		rest("/config/endpoints/{endpointId}/handledSituations/{situationId}")
				.get()
				.outType(HandledSituation.class)
				.to("bean:endpointApi?method=getHandledSituation(${header.situationId})");
		// ../endpoints/<id>/handledSituations/<id> -->DELETE: delete handled
		// situation
		rest("/config/endpoints/{endpointId}/handledSituations/{situationId}")
				.delete()
				.to("bean:endpointApi?method=deleteHandledSituation(${header.situationId})");
		// ../endpoints/<id>/handledSituations/<id> -->PUT: update handled
		// situation
		rest("/config/endpoints/{endpointId}/handledSituations/{situationId}")
				.put()
				.type(HandledSituation.class)
				.to("bean:endpointApi?method=updateHandledSituation(${header.situationId})");

	}

	private void createRuleApi() {
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

	}

	private void createPluginApi() {
		// ../plugins --> GET information about all plugins
		rest("/config/plugins").get().outTypeList(PluginInfo.class)
				.to("bean:pluginApi?method=getPlugins");

		// ../plugins --> POST: add a new Plugin
		rest("/config/plugins").post().consumes("multipart/form-data")
				.bindingMode(RestBindingMode.off)
				.to("bean:pluginApi?method=addPlugin");

		// ../plugins/<ID> --> GET information about the plugin with <ID>
		rest("/config/plugins/{pluginID}").get().outType(PluginInfo.class)
				.to("bean:pluginApi?method=getPluginByID(${header.pluginID})");

		// ../plugins/<id> --> DELETE: deletes the plugin with <id>
		rest("/config/plugins/{pluginID}").delete().to(
				"bean:pluginApi?method=deletePlugin(${header.pluginID}, ${header.delete})");

	}

	private void createHistoryApi() {
		rest("/config/history").get().outTypeList(HistoryEntry.class)
				.to("bean:historyApi?method=getHistory");

	}

	private void provideDocumentation() {
		from(
				component + ":http://" + host + ":" + port
						+ "/config/api-docs?chunked=false&enableCORS=true")
				.process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						if (swaggerDoc == null) {
							swaggerDoc = IOUtils.toString(this.getClass()
									.getClassLoader()
									.getResourceAsStream("swagger.json"));
						}
						exchange.getIn().setBody(swaggerDoc);
						exchange.getIn().setHeader("Content-Type",
								"application/json");
						exchange.getIn().setHeader("connection", "close");
					}
				});
	}
}

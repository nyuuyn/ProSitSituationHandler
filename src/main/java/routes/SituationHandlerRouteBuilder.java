package routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import situationHandling.OperationHandlerImpl;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

class SituationHandlerRouteBuilder extends RouteBuilder {

	public void configure() {

		// TODO: Hier den Endpunkt konfigurieren und den dann überall benutzen?
		// Wie geht es, da z.B. verschiedene Einstellungen zu benutzen?
		// Endpoint ep = endpoint("jetty:http://0.0.0.0:8080");

		// JettyHttpComponent jetty = new JettyHttpComponent8();

		// TODO: den gleichen server für alles benutzen

		// by using 0.0.0.0, the jetty server is exposed on all network
		// interfaces
		from("jetty:http://0.0.0.0:8080/SoapEndpoint?matchOnUriPrefix=true")
				.to("stream:out").bean(OperationHandlerImpl.class);
		/*
		 * REST Api Configuration
		 */
		// TODO: Was ist mit den Consumes/Produces dinger?
		rest("/config").description("Situation Handler RestAPI")
				.consumes("application/json").produces("application/json");

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

		// TODO: Dieses Put ist nicht idempotent?? Das heisst hier wäre wohl
		// eher Post angebracht?
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
		rest("/config/endpoints/{endpointId}")
				.delete()
				.to("bean:endpointApi?method=deleteEndpoint(${header.endpointId})");

		// ../endpoints/<id> --> PUT: updates the endpoint with <id>
		rest("/config/endpoints/{endpointId}")
				.put().type(Endpoint.class)
				.to("bean:endpointApi?method=updateEndpoint(${header.endpointId})");

		// TODO
		// Könnte man auch so machen, erfordert aber Bean registrierung
		// .get("/rules").to("bean:RuleAPI?method=getRules");

		restConfiguration().component("jetty").port(8081).host("0.0.0.0")
				.bindingMode(RestBindingMode.json)
				.dataFormatProperty("prettyPrint", "true");

	}

}

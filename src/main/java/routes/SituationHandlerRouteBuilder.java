package routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import situationHandling.OperationHandlerImpl;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Rule;
import api.configuration.RuleAPI;

public class SituationHandlerRouteBuilder extends RouteBuilder {

	public void configure() {

		// TODO: den gleichen server für alles benutzen

		// by using 0.0.0.0, the jetty server is exposed on all network
		// interfaces
		from("jetty:http://0.0.0.0:8080/SoapEndpoint?matchOnUriPrefix=true")
				.to("stream:out").bean(OperationHandlerImpl.class);

		// TODO: Was ist mit den Consumes/Produces dinger?
		rest("/config").description("Situation Handler RestAPI")
				.consumes("application/json").produces("application/json");

		// Rest Api operations
		// ../rules --> GET all rules
		rest("/config/rules").get().outTypeList(Rule.class)
				.to("bean:ruleApi?method=getRules");

		// ../rules/<ID> -->GET rules with <ID>
		rest("config/rules/{ruleId}").get().outType(Rule.class)
				.to("bean:ruleApi?method=getRuleByID(${header.ruleId})");
		// from("direct:getRuleByID").bean(RuleAPI.class,
		// "getRuleByID(${header.ruleId})");

		// ../rules/<ID>/actions --> GET all actions of rule <ID>
		rest("config/rules/{ruleId}/actions").get().outTypeList(Action.class)
				.to("bean:ruleApi?method=getActionsByRule(${header.ruleId})");
		// from("direct:getActionsByRule").bean(RuleAPI.class,
		// "getActionsByRule(${header.ruleId})");

		// ../rules/<ID>/actions/<actionID> --> GET action with <actionID>
		rest("config/rules/{ruleId}/actions/{actionId}").get()
				.outType(Action.class)
				.to("bean:ruleApi?method=getActionByID(${header.actionId})");
		// from("direct:getActionByID").bean(RuleAPI.class,
		// "getActionByID(${header.actionId})");

		// TODO
		// Könnte man auch so machen, erfordert aber Bean registrierung
		// .get("/rules").to("bean:RuleAPI?method=getRules");

		restConfiguration().component("jetty").port(8081).host("0.0.0.0")
				.bindingMode(RestBindingMode.json)
				.dataFormatProperty("prettyPrint", "true");

	}

}

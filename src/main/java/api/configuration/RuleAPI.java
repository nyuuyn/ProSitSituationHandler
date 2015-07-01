package api.configuration;

import java.util.LinkedList;

import org.apache.camel.Exchange;

import situationHandling.storage.RuleStorageAccess;
import situationHandling.storage.StorageAccessFactory;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Rule;
import situationHandling.storage.datatypes.Situation;

public class RuleAPI {

	RuleStorageAccess rsa;

	public RuleAPI() {
		this.rsa = StorageAccessFactory.getRuleStorageAccess();
	}

	// TODO Fehler Behandlung

	public void getRules(Exchange exchange) {
		exchange.getIn().setBody(rsa.getAllRules());
	}

	public void getRuleByID(Integer ruleID, Exchange exchange) {
		Rule rule = rsa.getRuleByID(ruleID);
		if (rule == null) {
			exchange.getIn().setBody("Rule " + ruleID + " not found.");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		} else {
			exchange.getIn().setBody(rule);
		}
	}

	public void addRule(Exchange exchange) {
		Rule rule = exchange.getIn().getBody(Rule.class);
		int ruleID = rsa.addRule(rule.getSituation(), rule.getActions());

		exchange.getIn().setBody(
				"Rule successfully added. New rule id is " + ruleID);
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
	}

	public void updateRuleSituation(Integer ruleID, Exchange exchange) {
		Situation situation = exchange.getIn().getBody(Situation.class);
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
		if (rsa.updateRuleSituation(ruleID, situation)) {
			exchange.getIn().setBody("Rule successfully updated");
		} else {
			exchange.getIn()
					.setBody(
							"Rule "
									+ ruleID
									+ " could not be updated. There is already an rule for this Situation or no rule with this id exists.");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		}
	}

	public void deleteRule(Integer ruleID, Exchange exchange) {

		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
		if (rsa.deleteRule(ruleID)) {
			exchange.getIn().setBody("Rule successfully deleted");
		} else {
			exchange.getIn()
					.setBody(
							"Rule "
									+ ruleID
									+ " could not be delete. There is rule with this id.");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		}
	}

	public void getActionsByRule(Integer ruleID, Exchange exchange) {
		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();
		exchange.getIn().setBody(rsa.getActionsByRuleID(ruleID));
	}

	public void getActionByID(Integer actionID, Exchange exchange) {
		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();

		Action action = rsa.getActionByID(actionID);
		if (action == null) {
			exchange.getIn().setBody("Action " + actionID + " not found.");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
		} else {
			exchange.getIn().setBody(action);

		}
	}

	public void addAction(Exchange exchange) {

	}

	public void deleteAction(Exchange exchange) {

	}

	public void updateAction(Exchange exchange) {

	}

}

class Test {

	String preName;
	String surName;

	public Test(String preName, String surName) {
		super();
		this.preName = preName;
		this.surName = surName;
	}

	public String getPreName() {
		return preName;
	}

	public void setPreName(String preName) {
		this.preName = preName;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

}

class ListWrapper {

	LinkedList<Test> liste;

	public LinkedList<Test> getListe() {
		return liste;
	}

	public void setListe(LinkedList<Test> liste) {
		this.liste = liste;
	}

	public ListWrapper(LinkedList<Test> liste) {
		this.liste = liste;
	}

}

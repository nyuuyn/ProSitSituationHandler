package api.configuration;

import java.util.LinkedList;

import org.apache.camel.Exchange;

import situationHandling.storage.RuleStorageAccess;
import situationHandling.storage.StorageAccessFactory;

public class RuleAPI {
	
	//TODO Fehler Behandlung


	public void getRules(Exchange exchange) {
		
		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();
		exchange.getIn().setBody(rsa.getAllRules());		
	}
	
	public void getRuleByID(Integer ruleID, Exchange exchange){
		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();
		exchange.getIn().setBody(rsa.getRuleByID(ruleID));
	}
	
	public void addRule(Exchange exchange){
		
	}
	
	public void updateRuleSituation(Exchange exchange){
		
	}
	
	public void deleteRule(Exchange exchange){
		
	}
	
	public void getActionsByRule (Integer ruleID, Exchange exchange){
		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();
		exchange.getIn().setBody(rsa.getActionsByRuleID(ruleID));
	}
	
	public void getActionByID (Integer actionID, Exchange exchange){
		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();
		exchange.getIn().setBody(rsa.getActionByID(actionID));
	}
	
	public void addAction(Exchange exchange){
		
	}
	
	public void deleteAction(Exchange exchange){
		
	}
	
	public void updateAction (Exchange exchange){
		
	}


}


class Test{
	
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

class ListWrapper{
	
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

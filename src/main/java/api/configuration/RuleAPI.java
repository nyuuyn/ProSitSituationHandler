package api.configuration;

import java.util.LinkedList;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import situationHandling.storage.RuleStorageAccess;
import situationHandling.storage.StorageAccessFactory;

public class RuleAPI {


	public void getRules(Exchange exchange) {
		

		
//		exchange.getIn().setBody("Enis", String.class);
		
		LinkedList<Test> liste = new LinkedList<>();
		liste.add(new Test("1", "2"));
		liste.add(new Test("3", "4"));
		
		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();
		
		
		exchange.getIn().setBody(rsa.getAllRules());
		
//		exchange.getIn().setBody(new ListWrapper(liste), ListWrapper.class);
		
	}
	
	public void getRuleByID(Exchange exchange){
		System.out.println("Header: " + exchange.getIn().getHeader("id"));
//		System.out.println("ID: " + id);


		
		exchange.getIn().setBody(new Test("1", "2"), Test.class);
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

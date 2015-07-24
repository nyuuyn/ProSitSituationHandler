package situationHandling.storage;

import java.util.HashMap;
import java.util.LinkedList;

import situationHandling.exceptions.InvalidActionException;
import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.exceptions.InvalidRuleException;
import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Action.ExecutionTime;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;
/**
 * Puts sample data in the db. However, probably not running anymore.
 * @author Stefan
 *
 */
@Deprecated
public class DatabaseFiller {

	public static void main(String[] args) {
		addSampleEndpoints();
		addSampleRule();
		System.exit(0);

	}

	private static void addSampleEndpoints() {
		// add two example endpoints
		EndpointStorageAccess esa = StorageAccessFactory
				.getEndpointStorageAccess();

		try {
			esa.addEndpoint(new Operation("test", "miniwebservice"),
					new Situation("situation1", "object1"),
					"http://localhost:4434/miniwebservice");
			esa.addEndpoint(new Operation("test", "miniwebservice"),
					new Situation("situation2", "object1"),
					"http://localhost:4435/miniwebservice");
		} catch (InvalidEndpointException e) {
			e.printStackTrace();
		}
	}

	private static void addSampleRule() {
		// add two sample rules
		// rule 1
		RuleStorageAccess rsa = StorageAccessFactory.getRuleStorageAccess();

		LinkedList<Action> actions = new LinkedList<>();

		HashMap<String, String> params = new HashMap<>();
		params.put("Email Subject", "Situation1 - Object 1");

		actions.add(new Action("situationHandler.gmail",
				"stefan.fuerst.89@gmail.com", "Situation1 at Object1 occured",
				ExecutionTime.onSituationChange, params));

		params = new HashMap<>();
		params.put("Http method", "POST");
		String soapBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:test=\"http://test/\">\r\n   <soapenv:Header/>\r\n   <soapenv:Body>\r\n      <test:hello>\r\n         <name>Stefan</name>\r\n      </test:hello>\r\n   </soapenv:Body>\r\n</soapenv:Envelope>";

		actions.add(new Action("situationHandler.http",
				"http://localhost:4434/miniwebservice", soapBody,
				ExecutionTime.onSituationChange, params));

		try {
			rsa.addRule(new Situation("situation1", "object1"), actions);
		} catch (InvalidRuleException e) {
			e.printStackTrace();
		} catch (InvalidActionException e) {
			e.printStackTrace();
		}
		// --------------
		// rule 2
		actions = new LinkedList<>();
		params = new HashMap<>();
		params.put("Email Subject", "Situation2 - Object 1");
		actions.add(new Action("situationHandler.gmail",
				"stefan.fuerst.89@gmail.com", "Situation2 at Object1 occured",
				ExecutionTime.onSituationChange, params));

		params = new HashMap<>();
		params.put("Http method", "POST");
		actions.add(new Action("situationHandler.http",
				"http://localhost:4435/miniwebservice", soapBody,
				ExecutionTime.onSituationChange, params));

		try {
			rsa.addRule(new Situation("situation2", "object1"), actions);
		} catch (InvalidRuleException e) {
			e.printStackTrace();
		} catch (InvalidActionException e) {
			e.printStackTrace();
		}
	}
}

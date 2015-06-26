package situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

public class EndpointStorageTest {

	public static void main(String[] args) {

		EndpointStorageAccess esa = StorageAccessFactory
				.getEndpointStorageAccess();
		
		ArrayList<Integer> ids = new ArrayList<>();
		
		Operation operation = new Operation("hello", "world");
		Situation situation = new Situation("TooHot", "Maschine1");
		
		URL endpointURL = null;
		
		try {
			endpointURL = new URL("http://example.com");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		System.out.println("Adding endpoint");
		int epID = esa.addEndpoint(operation, situation, endpointURL);
		ids.add(epID);

		System.out.println("Endpoint added. ID:" + epID);
		System.out.println("Endpoint Query");
		System.out.println("URL: "
				+ esa.getEndpointURL(situation, operation).toString());
		
		System.out.println("Adding further Endpoints");
		ids.add(esa.addEndpoint(operation, situation, endpointURL));
		ids.add(esa.addEndpoint(operation, situation, endpointURL));
		ids.add(esa.addEndpoint(operation, situation, endpointURL));
		
		System.out.println("Getting all Endpoints");
		esa.getAllEndpoints().forEach(ep -> System.out.println(ep.toString()));
		
		System.out.println("Update test");
		Operation operationUp = new Operation("bye", "world");
		Situation situationUp = new Situation("tooCold", "Maschine1");
		
		URL endpointURLUp = null;
		
		try {
			endpointURLUp = new URL("http://safhjkafhk.com");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		esa.updateEndpoint(ids.get(0), situationUp, null, null);
		esa.updateEndpoint(ids.get(1), null, operationUp, null);
		esa.updateEndpoint(ids.get(2), null, null, endpointURLUp);
		
		System.out.println("List after update:");
		esa.getAllEndpoints().forEach(ep -> System.out.println(ep.toString()));
		
		System.out.println("Deleting Endpoints");
		ids.forEach(id -> esa.deleteEndpoint(id));

		System.out.println("List after delete:");
		esa.getAllEndpoints().forEach(ep -> System.out.println(ep.toString()));
		
		System.exit(0);
	}
}

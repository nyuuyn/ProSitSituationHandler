package situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;

import situationHandling.storage.EndpointStorageAccess;
import situationHandling.storage.Operation;
import situationHandling.storage.Situation;
import situationHandling.storage.StorageAccessFactory;

public class StorageTest {

	public static void main(String[] args) {

		EndpointStorageAccess esa = StorageAccessFactory
				.getEndpointStorageAccess();

		Operation operation = new Operation("hello", "world");
		Situation situation = new Situation("TooHot", "Maschine1");

		URL endpointURL = null;

		System.out.println("Adding endpoint");
		try {
			endpointURL = new URL("http://example.com");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		int epID = esa.addEndpoint(operation, situation, endpointURL);

		System.out.println("Endpoint added. ID:" + epID);
		System.out.println("Endpoint Query");
		System.out.println("URL: "
				+ esa.getEndpointURL(situation, operation).toString());
		
		System.out.println("Adding further Endpoints");
		esa.addEndpoint(operation, situation, endpointURL);
		esa.addEndpoint(operation, situation, endpointURL);
		esa.addEndpoint(operation, situation, endpointURL);
		
		System.out.println("Getting all Endpoints");
		esa.getAllEndpoints().forEach(ep -> System.out.println(ep.toString()));

	}
}

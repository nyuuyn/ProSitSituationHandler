package situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

/**
 * The Class EndpointStorageTest tests the Endpoint storage by using the
 * different methods. Note that this is not a unit test. The test just runs all
 * methods. If no Exceptions etc. are thrown and the outputs looks ok, it is
 * assumed that everything works.
 */
public class EndpointStorageTest {

	/**
	 * The main method that runs the test.
	 *
	 * @param args
	 *            the arguments (not used)
	 */
	public static void main(String[] args) {

		EndpointStorageAccess esa = StorageAccessFactory
				.getEndpointStorageAccess();

		// testing error handling - using non-existent ids
		esa.deleteEndpoint(-1);
		esa.updateEndpoint(-1, null, null, null);

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

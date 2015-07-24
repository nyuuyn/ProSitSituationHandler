package situationHandling.storage;

import java.util.ArrayList;

import situationHandling.exceptions.InvalidEndpointException;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

/**
 * The Class EndpointStorageTest tests the Endpoint storage by using the
 * different methods. Note that this is not a unit test. The test just runs all
 * methods. If no Exceptions etc. are thrown and the outputs looks ok, it is
 * assumed that everything works.
 * @deprecated  probably not running anymore.
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
		try {
			// testing error handling - using non-existent ids
			esa.deleteEndpoint(-1);
			esa.updateEndpoint(-1, null, null, null);

			ArrayList<Integer> ids = new ArrayList<>();

			Operation operation = new Operation("hello", "world");
			Situation situation = new Situation("TooHot", "Maschine1");

			String endpointURL = "http://example.com";

			System.out.println("Adding endpoint");
			int epID;
//			epID = esa.addEndpoint(operation, situation, endpointURL);
//			ids.add(epID);

//			System.out.println("Endpoint added. ID:" + epID);
			System.out.println("Endpoint Query");
			System.out.println("URL: "
					+ esa.getEndpointURL(situation, operation).toString());

			// Getting Endpoint by ID
			System.out.println("Getting Endpoint by ID");
//			System.out.println(esa.getEndpointByID(epID));

			System.out.println("Adding further Endpoints");

//			ids.add(esa.addEndpoint(operation, situation, endpointURL));
//			ids.add(esa.addEndpoint(operation, situation, endpointURL));
//			ids.add(esa.addEndpoint(operation, situation, endpointURL));

			System.out.println("Getting all Endpoints");
			esa.getAllEndpoints().forEach(
					ep -> System.out.println(ep.toString()));

			System.out.println("Update test");
			Operation operationUp = new Operation("bye", "world");
			Situation situationUp = new Situation("tooCold", "Maschine1");

			String endpointURLUp = "http://safhjkafhk.com";

//			esa.updateEndpoint(ids.get(0), situationUp, null, null);
			esa.updateEndpoint(ids.get(1), null, operationUp, null);
			esa.updateEndpoint(ids.get(2), null, null, endpointURLUp);

			System.out.println("List after update:");
			esa.getAllEndpoints().forEach(
					ep -> System.out.println(ep.toString()));

			System.out.println("Deleting Endpoints");
			ids.forEach(id -> esa.deleteEndpoint(id));

			System.out.println("List after delete:");
			esa.getAllEndpoints().forEach(
					ep -> System.out.println(ep.toString()));
		} catch (InvalidEndpointException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}

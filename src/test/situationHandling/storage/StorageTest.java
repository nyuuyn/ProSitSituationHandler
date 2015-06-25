package test.situationHandling.storage;

import java.net.MalformedURLException;
import java.net.URL;

import situationHandling.storage.EndpointStorageAccess;
import situationHandling.storage.Operation;
import situationHandling.storage.Situation;
import situationHandling.storage.StorageAccessFactory;

public class StorageTest {
	


	public static void main(String[] args) {
		
		EndpointStorageAccess esa = StorageAccessFactory.getEndpointStorageAccess();
		
		try {
			esa.addEndpoint(new Operation("test", "test"), new Situation("sit", "obj"), new URL("http://example.com"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	
	}


}

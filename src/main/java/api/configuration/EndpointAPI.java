package api.configuration;

import situationHandling.storage.EndpointStorageAccess;
import situationHandling.storage.StorageAccessFactory;

public class EndpointAPI {

	EndpointStorageAccess esa;
	
	public EndpointAPI() {
		esa = StorageAccessFactory.getEndpointStorageAccess();
	}
	
	

}

package situationHandling.storage;

public class StorageAccessFactory {
	
	public static EndpointStorageAccess getEndpointStorageAccess(){
		return new EndpointStorageAccessImpl();
	}
	

}

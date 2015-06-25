package situationHandling.storage;

import java.net.URL;
import java.util.List;

import situationHandling.storage.datatypes.Endpoint;
import situationHandling.storage.datatypes.Operation;
import situationHandling.storage.datatypes.Situation;

public interface EndpointStorageAccess {

	public URL getEndpointURL(Situation situation, Operation operation);
	
	public List<Endpoint> getAllEndpoints ();

	public int addEndpoint(Operation operation, Situation situation, URL endpointURL);

	public boolean deleteEndpoint(int endpointID);

	public boolean updateEndpoint(int endpointID, Situation situation,
			Operation operation, URL endpointURL);
}

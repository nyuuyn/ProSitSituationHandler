package situationHandling.storage;

import java.net.URL;
import java.util.List;

public interface EndpointStorageAccess {

	public URL getEndpointURL(Situation situation, Operation operation);
	
	public List<Endpoint> getAllEndpoints ();

	public int addEndpoint(Operation operation, Situation situation, URL endpointURL);

	public boolean removeEndpoint(int endpointID);

	public boolean updateEndpoint(int endpointID, Situation situation,
			Operation operation, URL endpoint);
}

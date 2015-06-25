package situationHandling.storage;

import java.net.URL;
import java.util.List;
import java.util.UUID;

public interface EndpointStorageAccess {

	public URL getEndpointURL(Situation situation, Operation operation);
	
	public List<Endpoint> getAllEndpoints ();

	public int addEndpoint(Operation operation, Situation situation, URL endpointURL);

	public boolean removeEndpoint(UUID endpointID);

	public boolean updateEndpoint(UUID endpointID, Situation situation,
			Operation operation, URL endpoint);
}

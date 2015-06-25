package situationHandling.storage;

import java.net.URL;
import java.util.List;
import java.util.UUID;

class EndpointStorageAccessImpl implements EndpointStorageAccess {

	EndpointStorageAccessImpl() {
	}

	@Override
	public URL getEndpointURL(Situation situation, Operation operation) {
		return null;
	}

	@Override
	public List<Endpoint> getAllEndpoints() {
		return null;
	}

	@Override
	public UUID addEndpoint(Endpoint endpoint) {
		return null;
	}

	@Override
	public boolean removeEndpoint(UUID endpointID) {
		return false;
	}

	@Override
	public boolean updateEndpoint(UUID endpointID, Situation situation,
			Operation operation, URL endpoint) {
		return false;
	}

}

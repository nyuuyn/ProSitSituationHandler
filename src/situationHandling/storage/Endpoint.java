package situationHandling.storage;

import java.net.URL;
import java.util.UUID;

public class Endpoint {

	private URL endpointURL;

	private Situation situation;

	private Operation operation;
	
	private UUID endpointUUID;

	public Endpoint(URL endpointURL, Situation situation, Operation operation,
			UUID endpointUUID) {
		this.endpointURL = endpointURL;
		this.situation = situation;
		this.operation = operation;
		this.endpointUUID = endpointUUID;
	}

	public Endpoint() {
		super();
	}

	public URL getEndpointURL() {
		return endpointURL;
	}

	public void setEndpointURL(URL endpointURL) {
		this.endpointURL = endpointURL;
	}

	public Situation getSituation() {
		return situation;
	}

	public void setSituation(Situation situation) {
		this.situation = situation;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public UUID getEndpointUUID() {
		return endpointUUID;
	}

	public void setEndpointUUID(UUID endpointUUID) {
		this.endpointUUID = endpointUUID;
	}
	
}

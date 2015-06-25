package situationHandling.storage;

import javax.persistence.*;

@Entity
@Table(name = "endpoints")
public class Endpoint {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int endpointID;

	// situation
	@Column(name = "situation_name")
	private String situationName;

	@Column(name = "object_name")
private String objectName;

	// operation
	@Column(name = "operation_name")
	private String operationName;
	@Column(name = "qualifier")
	private String qualifier;
	
	@Column(name = "endpoint_URL")
	private String endpointURL;

	public Endpoint(String endpointURL, String situationName,
			String objectName, String operationName, String qualifier) {
		this.endpointURL = endpointURL;
		this.situationName = situationName;
		this.objectName = objectName;
		this.operationName = operationName;
		this.qualifier = qualifier;
	}

	public Endpoint(String endpointURL, Situation situation, Operation operation) {
		this.endpointURL = endpointURL;

		this.situationName = situation.getSituationName();
		this.objectName = situation.getObjectName();

		this.operationName = operation.getOperationName();
		this.qualifier = operation.getQualifier();

	}

	public Endpoint() {
		super();
	}

	public String getEndpointURL() {
		return endpointURL;
	}

	public void setEndpointURL(String endpointURL) {
		this.endpointURL = endpointURL;
	}

	public Situation getSituation() {
		return new Situation(situationName, objectName);
	}

	public void setSituation(Situation situation) {
		this.situationName = situation.getSituationName();
		this.objectName = situation.getObjectName();
	}

	public Operation getOperation() {
		return new Operation(operationName, qualifier);
	}

	public void setOperation(Operation operation) {
		this.operationName = operation.getOperationName();
		this.qualifier = operation.getQualifier();
	}

	public int getEndpointID() {
		return endpointID;
	}

	public void setEndpointID(int endpointID) {
		this.endpointID = endpointID;
	}

	public String getSituationName() {
		return situationName;
	}

	public void setSituationName(String situationName) {
		this.situationName = situationName;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	@Override
	public String toString() {

		return "[" + endpointID + " | " + situationName + " | " + objectName
				+ " | " + operationName + " | " + qualifier + " | "
				+ endpointURL.toString() + "]";
	}

}

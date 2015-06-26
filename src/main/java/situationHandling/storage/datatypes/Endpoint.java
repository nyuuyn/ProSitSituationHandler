package situationHandling.storage.datatypes;

import javax.persistence.*;

/**
 * A wrapper class that contains all information about an endpoint for a soap
 * message. Stores the information that is required to forward a soap message
 * from a workflow to an arbitrary endpoint that accepts the message received by
 * the workflow. <div>
 *
 * An endpoint is decribed by the following information:
 * <ul>
 * <li>
 * The Situation. The situation in which an endpoint is used. A situation
 * consists of a situation name the name of an object.</li>
 * <li>
 * The Operation. The operation of the endpoint to be used. A operation consists
 * of the operation name and a qualifier, like namespace, porttype etc..</li>
 * <li>
 * The endpoint URL. Messages will be forwarded to the URL.</li>
 * </ul>
 * 
 * 
 *
 * @author Stefan
 * @see Situation
 * @see Operation
 */
/*
 * An instance of Endpoint can be mapped to the table endpoints using JPA.
 */
@Entity
@Table(name = "endpoints")
public class Endpoint {

	/**
	 * The endpoint id. Used as primary key in the database. Never set this
	 * manually. The database will generate this value.
	 */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int endpointID;

	/** The situation name. */
	@Column(name = "situation_name")
	private String situationName;

	/** The object name. */
	@Column(name = "object_name")
	private String objectName;

	/** The operation name. */
	// operation
	@Column(name = "operation_name")
	private String operationName;

	/** The qualifier, like namespace etc. */
	@Column(name = "qualifier")
	private String qualifier;

	/** The endpoint url. */
	@Column(name = "endpoint_url")
	private String endpointURL;

	/**
	 * Instantiates a new endpoint. Allows to specify the name of the situation
	 * etc. See documentation of the class for the description of the
	 * parameters.
	 *
	 * @param endpointURL
	 *            the endpoint url. This MUST be a valid URL. Otherwise,
	 *            fowarding the message will fail
	 * @param situationName
	 *            the situation name
	 * @param objectName
	 *            the object name
	 * @param operationName
	 *            the operation name
	 * @param qualifier
	 *            the qualifier
	 */
	public Endpoint(String endpointURL, String situationName,
			String objectName, String operationName, String qualifier) {
		this.endpointURL = endpointURL;
		this.situationName = situationName;
		this.objectName = objectName;
		this.operationName = operationName;
		this.qualifier = qualifier;
	}

	/**
	 * Instantiates a new endpoint. See documentation of the class for the
	 * description of the parameters.
	 *
	 * @param endpointURL
	 *            the endpoint url.This MUST be a valid URL. Otherwise,
	 *            fowarding the message will fail.
	 * @param situation
	 *            the situation
	 * @param operation
	 *            the operation
	 */
	public Endpoint(String endpointURL, Situation situation, Operation operation) {
		this.endpointURL = endpointURL;

		this.situationName = situation.getSituationName();
		this.objectName = situation.getObjectName();

		this.operationName = operation.getOperationName();
		this.qualifier = operation.getQualifier();

	}

	/**
	 * Instantiates a new endpoint. The default construtor. Set the fields
	 * manually.
	 */
	public Endpoint() {
		super();
	}

	/**
	 * Gets the endpoint url.
	 *
	 * @return the endpoint url
	 */
	public String getEndpointURL() {
		return endpointURL;
	}

	/**
	 * Sets the endpoint url.
	 *
	 * @param endpointURL
	 *            the new endpoint url
	 */
	public void setEndpointURL(String endpointURL) {
		this.endpointURL = endpointURL;
	}

	/**
	 * Gets the situation.
	 *
	 * @return the situation
	 */
	public Situation getSituation() {
		return new Situation(situationName, objectName);
	}

	/**
	 * Sets the situation.
	 *
	 * @param situation
	 *            the new situation
	 */
	public void setSituation(Situation situation) {
		this.situationName = situation.getSituationName();
		this.objectName = situation.getObjectName();
	}

	/**
	 * Gets the operation.
	 *
	 * @return the operation
	 */
	public Operation getOperation() {
		return new Operation(operationName, qualifier);
	}

	/**
	 * Sets the operation.
	 *
	 * @param operation
	 *            the new operation
	 */
	public void setOperation(Operation operation) {
		this.operationName = operation.getOperationName();
		this.qualifier = operation.getQualifier();
	}

	/**
	 * Gets the endpoint id. The id uniquely identifies the endpoint.
	 *
	 * @return the endpoint id
	 */
	public int getEndpointID() {
		return endpointID;
	}

	/**
	 * Sets the endpoint id. The id uniquely identifies the endpoint. The id is
	 * used in the database. Never set this value manually when creating an
	 * endpoint instance.
	 *
	 * @param endpointID
	 *            the new endpoint id
	 */
	public void setEndpointID(int endpointID) {
		this.endpointID = endpointID;
	}

	/**
	 * Gets the situation name.
	 *
	 * @return the situation name
	 */
	public String getSituationName() {
		return situationName;
	}

	/**
	 * Sets the situation name.
	 *
	 * @param situationName
	 *            the new situation name
	 */
	public void setSituationName(String situationName) {
		this.situationName = situationName;
	}

	/**
	 * Gets the object name.
	 *
	 * @return the object name
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * Sets the object name.
	 *
	 * @param objectName
	 *            the new object name
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * Gets the operation name.
	 *
	 * @return the operation name
	 */
	public String getOperationName() {
		return operationName;
	}

	/**
	 * Sets the operation name.
	 *
	 * @param operationName
	 *            the new operation name
	 */
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	/**
	 * Gets the qualifier.
	 *
	 * @return the qualifier
	 */
	public String getQualifier() {
		return qualifier;
	}

	/**
	 * Sets the qualifier.
	 *
	 * @param qualifier
	 *            the new qualifier
	 */
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "[" + endpointID + " | " + situationName + " | " + objectName
				+ " | " + operationName + " | " + qualifier + " | "
				+ endpointURL.toString() + "]";
	}

}

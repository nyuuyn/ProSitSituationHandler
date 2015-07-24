package situationHandling.storage.datatypes;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * TODO: DOKU f�r neuen Endpunkt aktualisieren A wrapper class that contains all
 * information about an endpoint for a soap message. Stores the information that
 * is required to forward a soap message from a workflow to an arbitrary
 * endpoint that accepts the message received by the workflow. <div>
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

	/**
	 * TODO
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "endpoint_id")
	private List<HandledSituation> situations;

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

	/**TODO
	 * Instantiates a new endpoint. Allows to specify the name of the situation
	 * etc. See documentation of the class for the description of the
	 * parameters.
	 *
	 * @param endpointURL
	 *            the endpoint url. This MUST be a valid URL. Otherwise,
	 *            fowarding the message will fail
	 * @param operationName
	 *            the operation name
	 * @param qualifier
	 *            the qualifier
	 */
	public Endpoint(String endpointURL, List<HandledSituation> situations,
			String operationName, String qualifier) {
		this.endpointURL = endpointURL;
		this.situations = situations;
		this.operationName = operationName;
		this.qualifier = qualifier;
	}

	/**TODO
	 * Instantiates a new endpoint. See documentation of the class for the
	 * description of the parameters.
	 *
	 * @param endpointURL
	 *            the endpoint url.This MUST be a valid URL. Otherwise,
	 *            fowarding the message will fail.
	 * @param operation
	 *            the operation
	 */
	public Endpoint(String endpointURL, List<HandledSituation> situations,
			Operation operation) {
		this.endpointURL = endpointURL;

		this.situations = situations;

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
	
	

	/**TODO
	 * @return the situations
	 */
	public List<HandledSituation> getSituations() {
		return situations;
	}

	/**TODO
	 * @param situations the situations to set
	 */
	public void setSituations(List<HandledSituation> situations) {
		this.situations = situations;
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

		return "[" + endpointID + " | " + operationName + " | " + qualifier
				+ " | " + endpointURL.toString() + situations.toString() + "]";
	}

}

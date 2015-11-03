package situationHandling.storage.datatypes;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A wrapper class that contains all information about an endpoint for a soap
 * message. Stores the information that is required to forward a soap message
 * from a workflow to an arbitrary endpoint that accepts the message received by
 * the workflow. <div>
 *
 * An endpoint is decribed by the following information:
 * <ul>
 * <li>Endpoint name: an expressive name for the endpoint</li>
 * <li>Endpoint description: describes what the endpoint does</li>
 * <li>One or more handled situations. The situations in which an endpoint is
 * used. A situation consists of a situation name the name of an object.
 * Furthermore a situation can be optional, i.e. the endpoint can also be used
 * when the situation did not occur. A situation can hold or not (i.e. it
 * appeared or it did not). When a situation changes it is possible that the
 * endpoint cannot be used anymore and a rollback has to be triggered.<br>
 * An endpoint can only be used if ALL handled situations occured (or not). This
 * means the handled situations are combined with a logical "AND" expression.
 * </li>
 * <li>The Operation. The operation of the endpoint to be used. A operation
 * consists of the operation name and a qualifier, like namespace, porttype
 * etc..</li>
 * <li>The endpoint URL. Messages will be forwarded to the URL.</li>
 * </ul>
 * 
 * An endpoint can have different states:
 * <ul>
 * <li>available: in this case the endpoint is available for immediate use. An
 * endpoint URL MUST be stated.</li>
 * <li>archive: In this case, the endpoint is available as archive and must be
 * deployed before using it. An archiveFilename MUST be stated.</li>
 * </ul>
 * 
 *
 * @author Stefan
 * @see Situation
 * @see HandledSituation
 * @see Operation
 * @see EndpointStatus
 */
/*
 * An instance of Endpoint can be mapped to the table endpoints using JPA.
 */
@Entity
@Table(name = "endpoints")
public class Endpoint {

    /**
     * The status of an endpoint. An endpoint is either "available" for
     * immediate use or it is only available as "archive" and needs to be
     * deployed before using it.
     */
    public enum EndpointStatus {
	archive, available
    }

    /**
     * The endpoint id. Used as primary key in the database. Never set this
     * manually. The database will generate this value.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int endpointID;

    /**
     * The name of the endpoint.
     */
    @Column(name = "endpoint_name")
    private String endpointName;

    /**
     * The description of the endpoint.
     */
    @Column(name = "endpoint_description")
    private String endpointDescription;

    /**
     * The list of situations handled by this endpoint.
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

    /**
     * the name of the archive that contains the proces, in case the status is
     * "archive"
     */
    @Column(name = "archive_filename")
    private String archiveFilename;

    /**
     * The status of the endpoint
     */
    @Column(name = "endpoint_status")
    @Enumerated(EnumType.STRING)
    private EndpointStatus endpointStatus;

    /**
     * Instantiates a new endpoint. Allows to specify the situations etc. See
     * documentation of the class for the description of the parameters.
     * 
     * @param endpointName
     *            the name of the endpoint.
     * @param endpointDescription
     *            the description of the endpoint
     * @param endpointURL
     *            the endpoint url. This MUST be a valid URL. Otherwise,
     *            fowarding the message will fail. Only necessary, when the
     *            status of the endpoint is "available".
     * @param operationName
     *            the operation name
     * @param situations
     *            list of situations handled by this endpoint.
     * @param endpointStatus
     *            the status of the endpoint
     * @param archiveFilename
     *            the name of the archive that contains the proces, in case the
     *            status is "archive"
     * @param qualifier
     *            the qualifier
     */
    public Endpoint(String endpointName, String endpointDescription, String endpointURL,
	    List<HandledSituation> situations, String operationName, String qualifier,
	    String archiveFilename, EndpointStatus endpointStatus) {
	this.endpointName = endpointName;
	this.endpointDescription = endpointDescription;
	this.endpointURL = endpointURL;
	this.situations = situations;
	this.operationName = operationName;
	this.qualifier = qualifier;
	this.archiveFilename = archiveFilename;
	this.endpointStatus = endpointStatus;
    }

    /**
     * Instantiates a new endpoint. See documentation of the class for the
     * description of the parameters.
     * 
     * @param endpointName
     *            the name of the endpoint.
     * @param endpointDescription
     *            the description of the endpoint
     * @param endpointURL
     *            the endpoint url.This MUST be a valid URL. Otherwise,
     *            fowarding the message will fail.
     * @param situations
     *            of situations handled by this endpoint.
     * @param endpointStatus
     *            the status of the endpoint
     * @param archiveFilename
     *            the name of the archive that contains the proces, in case the
     *            status is "archive"
     * @param operation
     *            the operation
     */
    public Endpoint(String endpointName, String endpointDescription, String endpointURL,
	    List<HandledSituation> situations, Operation operation, String archiveFilename,
	    EndpointStatus endpointStatus) {
	this.endpointName = endpointName;
	this.endpointDescription = endpointDescription;
	this.endpointURL = endpointURL;
	this.situations = situations;
	this.operationName = operation.getOperationName();
	this.qualifier = operation.getQualifier();
	this.archiveFilename = archiveFilename;
	this.endpointStatus = endpointStatus;
    }

    /**
     * Instantiates a new endpoint. The default construtor. Set the fields
     * manually.
     */
    public Endpoint() {
	super();
    }

    /**
     * 
     * Sets the name of the endpoint.
     * 
     * @param endpointName
     *            the endpointName to set
     */
    public void setEndpointName(String endpointName) {
	this.endpointName = endpointName;
    }

    /**
     * Gets the name of the endpoint.
     * 
     * @return the endpointName
     */
    public String getEndpointName() {
	return endpointName;
    }

    /**
     * Sets the description of the endpoint. Describes what the endpoint does.
     * 
     * @param endpointDescription
     *            the endpointDescription to set
     */
    public void setEndpointDescription(String endpointDescription) {
	this.endpointDescription = endpointDescription;
    }

    /**
     * Gets the description of the endpoint. Describes what the endpoint does.
     * 
     * @return the endpointDescription
     */
    public String getEndpointDescription() {
	return endpointDescription;
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
    @JsonIgnore
    public Operation getOperation() {
	return new Operation(operationName, qualifier);
    }

    /**
     * Sets the operation.
     *
     * @param operation
     *            the new operation
     */
    @JsonIgnore
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
     * 
     * @return list of situations handled by this endpoint.
     */
    public List<HandledSituation> getSituations() {
	return situations;
    }

    /**
     * Get all situations of this endpoint that require a rollback when
     * changing.
     * 
     * @return a list of situations
     */
    @JsonIgnore
    public List<Situation> getRollbackSituations() {
	LinkedList<Situation> rollbackSituations = new LinkedList<>();
	for (HandledSituation handledSit : situations) {
	    if (handledSit.isRollbackOnChange()) {
		rollbackSituations.add(handledSit.getSituation());
	    }
	}

	return rollbackSituations;
    }

    /**
     * 
     * @param situations
     *            list of situations handled by this endpoint.
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

    /**
     * @return the archiveFilename
     */
    public String getArchiveFilename() {
	return archiveFilename;
    }

    /**
     * @param archiveFilename
     *            the archiveFilename to set
     */
    public void setArchiveFilename(String archiveFilename) {
	this.archiveFilename = archiveFilename;
    }

    /**
     * @return the endpointStatus
     */
    public EndpointStatus getEndpointStatus() {
	return endpointStatus;
    }

    /**
     * @param endpointStatus
     *            the endpointStatus to set
     */
    public void setEndpointStatus(EndpointStatus endpointStatus) {
	this.endpointStatus = endpointStatus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Endpoint [endpointID=" + endpointID + ", endpointName=" + endpointName
		+ ", endpointDescription=" + endpointDescription + ", situations=" + situations
		+ ", operationName=" + operationName + ", qualifier=" + qualifier + ", endpointURL="
		+ endpointURL + ", archiveFilename=" + archiveFilename + ", endpointStatus="
		+ endpointStatus + "]";
    }

}

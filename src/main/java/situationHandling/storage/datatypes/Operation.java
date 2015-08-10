package situationHandling.storage.datatypes;

/**
 * The Class Operation is a wrapper class for operations from soap webservices.
 * An operation consists of an operation name and a qualifier for the name.
 * There are two options to specify an operation: either with a combination of
 * namespace and operation name (as stated in the soap message) or with the
 * wsa:Action header. In case namespace and operation name are used, the
 * qualifier is the namespace. In case the wsa:Action header is used, the
 * qualifier must be "wsa:Action" and the operation name is the url or whatever.
 * <p>
 * Note that it is prefered to use the wsa:Action header.
 */
public class Operation {

	/** The operation name. */
	private String operationName;

	/** The qualifier, like namespace etc. */
	private String qualifier;

	/**
	 * Instantiates a new operation. The default constructor. Set the fields
	 * manually.
	 */
	public Operation() {
		super();
	}

	/**
	 * Instantiates a new operation by specifiying an operation name and a
	 * qualifier.
	 *
	 * @param operationName
	 *            the operation name
	 * @param qualifier
	 *            the qualifier
	 */
	public Operation(String operationName, String qualifier) {
		this.operationName = operationName;
		this.qualifier = qualifier;
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
		return operationName + ":" + qualifier;
	}

}

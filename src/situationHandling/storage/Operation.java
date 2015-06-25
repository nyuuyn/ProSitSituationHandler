package situationHandling.storage;

public class Operation {

	private String operationName;
	private String qualifier;

	public Operation() {
		super();
	}

	public Operation(String operationName, String qualifier) {
		this.operationName = operationName;
		this.qualifier = qualifier;
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

}

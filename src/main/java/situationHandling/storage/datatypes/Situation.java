package situationHandling.storage.datatypes;

public class Situation {

	private String situationName;
	private String objectName;

	public Situation() {
		super();
	}

	public Situation(String situationName, String objectName) {
		super();
		this.situationName = situationName;
		this.objectName = objectName;
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

}

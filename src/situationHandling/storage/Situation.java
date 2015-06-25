package situationHandling.storage;

public class Situation {

	private String situation;
	private String object;

	public Situation() {
		super();
	}

	public Situation(String situation, String object) {
		super();
		this.situation = situation;
		this.object = object;
	}

	public String getSituation() {
		return situation;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

}

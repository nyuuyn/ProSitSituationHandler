package situationHandling.storage.datatypes;

/**
 * The Class Situation is used as wrapper to describe a situation. A situation
 * is described by the combination of a situation name and an object name.
 * 
 * <div>The situation name is the description of the situation, for example
 * "too_hot". The object name is the object the situation name refers to, for
 * example "machine_1". By specifying a situation like this, it is stated that
 * machine 1 is too hot.
 */
public class Situation {

	/** The situation name. */
	private String situationName;

	/** The object name. */
	private String objectName;

	/**
	 * Instantiates a new situation. Default constructor. Specify situation name
	 * and object name using the setters.
	 */
	public Situation() {
		super();
	}

	/**
	 * Instantiates a new situation by specifying the situation and the object
	 * name.
	 *
	 * @param situationName
	 *            the situation name
	 * @param objectName
	 *            the object name
	 */
	public Situation(String situationName, String objectName) {
		super();
		this.situationName = situationName;
		this.objectName = objectName;
	}

	/**
	 * Gets the situation name. The situation name is the description of a
	 * situation.
	 *
	 * @return the situation name
	 */
	public String getSituationName() {
		return situationName;
	}

	/**
	 * Sets the situation name.The situation name is the description of a
	 * situation.
	 *
	 * @param situationName
	 *            the new situation name
	 */
	public void setSituationName(String situationName) {
		this.situationName = situationName;
	}

	/**
	 * Gets the object name. The object name specifies the object this situation
	 * refers to.
	 *
	 * @return the object name
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * Sets the object name.The object name specifies the object this situation
	 * refers to.
	 *
	 * @param objectName
	 *            the new object name
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Situation [situationName=" + situationName + ", objectName="
				+ objectName + "]";
	}

}

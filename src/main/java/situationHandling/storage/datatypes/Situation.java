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
	private final String situationName;

	/** The object name. */
	private final String objectName;


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
	 * Gets the object name. The object name specifies the object this situation
	 * refers to.
	 *
	 * @return the object name
	 */
	public String getObjectName() {
		return objectName;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		//auto generated
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((objectName == null) ? 0 : objectName.hashCode());
		result = prime * result
				+ ((situationName == null) ? 0 : situationName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		//auto generated
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Situation)) {
			return false;
		}
		Situation other = (Situation) obj;
		if (objectName == null) {
			if (other.objectName != null) {
				return false;
			}
		} else if (!objectName.equals(other.objectName)) {
			return false;
		}
		if (situationName == null) {
			if (other.situationName != null) {
				return false;
			}
		} else if (!situationName.equals(other.situationName)) {
			return false;
		}
		return true;
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

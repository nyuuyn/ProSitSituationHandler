package situationHandling.storage.datatypes;

/**
 * The Class Situation is used as wrapper to describe a situation. A situation
 * is described by the combination of a situation name and an object id.
 * 
 * <div>The situation name is the description of the situation, for example
 * "too_hot". The object id is the object the situation name refers to, for
 * example "machine_1". By specifying a situation like this, it is stated that
 * machine 1 is too hot.
 */
public class Situation {

    /** The situation name. */
    private String situationName;

    /** The object id. */
    private String objectId;

    /**
     * Instantiates a new situation. Default constructor. Specify situation name
     * and object id using the setters.
     */
    public Situation() {
    }

    /**
     * Instantiates a new situation by specifying the situation and the object
     * name.
     *
     * @param situationName
     *            the situation name
     * @param objectId
     *            the object id
     */
    public Situation(String situationName, String objectId) {
	super();
	this.situationName = situationName;
	this.objectId = objectId;
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
     * Gets the object id. The object id specifies the object this situation
     * refers to.
     *
     * @return the object id
     */
    public String getObjectId() {
	return objectId;
    }

    /**
     * Sets the object id.The object id specifies the object this situation
     * refers to.
     *
     * @param objectId
     *            the new object id
     */
    public void setObjectId(String objectId) {
	this.objectId = objectId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	// auto generated
	final int prime = 31;
	int result = 1;
	result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
	result = prime * result + ((situationName == null) ? 0 : situationName.hashCode());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	// auto generated
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
	if (objectId == null) {
	    if (other.objectId != null) {
		return false;
	    }
	} else if (!objectId.equals(other.objectId)) {
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
	return "Situation [situationName=" + situationName + ", objectId=" + objectId + "]";
    }

}

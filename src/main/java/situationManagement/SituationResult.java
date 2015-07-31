package situationManagement;

import java.util.List;

import situationHandling.storage.datatypes.Situation;

/**
 * The Class SituationResult is a wrapper class for the result that is delivered
 * by the SRS when a situation is queried. It contains all information that is
 * delivered by the SRS and allows a direct mapping from the JSON Answer of the
 * SRS to an instance of this class.
 */
class SituationResult {

	/** The id of the situation. */
	private String _id;

	/** Meaning unkown */
	private String _rev;

	/** The id of the thing the situation relates to. */
	private String thing;

	/** The timestamp indicates the last change of situation. */
	private String timestamp;

	/** The id of situationtemplate that defines the situation. */
	private String situationtemplate;

	/** States whether the situation occurred or not. */
	private boolean occured;

	/** The name of the situation. */
	private String name;

	/** The sensorvalues recorded by the SRS. */
	private List<String> sensorvalues;

	/**
	 * Instantiates a new situation result. Use the setters to init the fields.
	 */
	public SituationResult() {

	}

	/**
	 * Instantiates a new situation result with instantiatied fields.
	 *
	 * @param _id
	 *            the id of the situation
	 * @param _rev
	 *            the _rev. Meaning unkown.
	 * @param thing
	 *            The id of the thing the situation relates to.
	 * @param timestamp
	 *            The timestamp indicates the last change of situation.
	 * @param situationtemplate
	 *            The id of situationtemplate that defines the situation.
	 * @param occured
	 *            States whether the situation occurred or not.
	 * @param name
	 *            The name of the situation.
	 * @param sensorvalues
	 *            The sensorvalues recorded by the SRS.
	 */
	SituationResult(String _id, String _rev, String thing, String timestamp, String situationtemplate, boolean occured,
			String name, List<String> sensorvalues) {
		this._id = _id;
		this._rev = _rev;
		this.thing = thing;
		this.timestamp = timestamp;
		this.situationtemplate = situationtemplate;
		this.occured = occured;
		this.name = name;
		this.sensorvalues = sensorvalues;
	}

	/**
	 * Gets the the id of the situation.
	 *
	 * @return the the id of the situation.
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * Sets the the id of the situation.
	 *
	 * @param _id
	 *            the id of the situation.
	 */
	public void set_id(String _id) {
		this._id = _id;
	}

	/**
	 * Gets the _rev. Meaning unkown.
	 * 
	 * @return the _rev
	 */
	public String get_rev() {
		return _rev;
	}

	/**
	 * Sets the _rev. Meaning unkown.
	 *
	 * @param _rev
	 *            the _rev to set
	 */
	public void set_rev(String _rev) {
		this._rev = _rev;
	}

	/**
	 * Gets The id of the thing the situation relates to.
	 *
	 * @return the thing
	 */
	public String getThing() {
		return thing;
	}

	/**
	 * Sets The id of the thing the situation relates to.
	 *
	 * @param thing
	 *            the thing to set
	 */
	public void setThing(String thing) {
		this.thing = thing;
	}

	/**
	 * Gets the timestamp. The timestamp indicates the last change of situation.
	 *
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp. The timestamp indicates the last change of situation.
	 *
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Gets the situationtemplate. The id of situationtemplate that defines the
	 * situation.
	 *
	 * @return the situationtemplate
	 */
	public String getSituationtemplate() {
		return situationtemplate;
	}

	/**
	 * Sets the situationtemplate. The id of situationtemplate that defines the
	 * situation.
	 *
	 * @param situationtemplate
	 *            the situationtemplate to set
	 */
	public void setSituationtemplate(String situationtemplate) {
		this.situationtemplate = situationtemplate;
	}

	/**
	 * Checks if occured. States whether the situation occurred or not.
	 *
	 * @return the occured
	 */
	public boolean isOccured() {
		return occured;
	}

	/**
	 * Sets occured. States whether the situation occurred or not.
	 *
	 * @param occured
	 *            the occured to set
	 */
	public void setOccured(boolean occured) {
		this.occured = occured;
	}

	/**
	 * Gets The name of the situation.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets The name of the situation.
	 *
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets The sensorvalues recorded by the SRS
	 *
	 * @return the sensorvalues
	 */
	public List<String> getSensorvalues() {
		return sensorvalues;
	}

	/**
	 * Sets The sensorvalues recorded by the SRS
	 *
	 * @param sensorvalues
	 *            the sensorvalues to set
	 */
	public void setSensorvalues(List<String> sensorvalues) {
		this.sensorvalues = sensorvalues;
	}

	/**
	 * Gets the situation described by the situation result.
	 *
	 * @return the situation
	 */
	public Situation getSituation() {
		return new Situation(situationtemplate, thing);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SituationResult [_id=" + _id + ", _rev=" + _rev + ", thing=" + thing + ", timestamp=" + timestamp
				+ ", situationtemplate=" + situationtemplate + ", occured=" + occured + ", sensorvalues=" + sensorvalues
				+ "]";
	}

}

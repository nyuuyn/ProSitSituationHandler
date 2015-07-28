package situationManagement;

import java.util.List;

class SituationResult {

	//TODO: Wrapper Klasse für dsa was vom SRS kommt
	
	
	private String _id;
	private String _rev;
	private String thing;
	private String timestamp;
	private String situationtemplate;
	private boolean occured;
	private List <String> sensorvalues;
	
	/**
	 * 
	 */
	SituationResult() {

	}
	
	/**
	 * @param _id
	 * @param _rev
	 * @param thing
	 * @param timestamp
	 * @param situationtemplate
	 * @param occured
	 * @param sensorvalues
	 */
	SituationResult(String _id, String _rev, String thing, String timestamp,
			String situationtemplate, boolean occured, List<String> sensorvalues) {
		this._id = _id;
		this._rev = _rev;
		this.thing = thing;
		this.timestamp = timestamp;
		this.situationtemplate = situationtemplate;
		this.occured = occured;
		this.sensorvalues = sensorvalues;
	}
	/**
	 * @return the _id
	 */
	String get_id() {
		return _id;
	}
	/**
	 * @param _id the _id to set
	 */
	void set_id(String _id) {
		this._id = _id;
	}
	/**
	 * @return the _rev
	 */
	String get_rev() {
		return _rev;
	}
	/**
	 * @param _rev the _rev to set
	 */
	void set_rev(String _rev) {
		this._rev = _rev;
	}
	/**
	 * @return the thing
	 */
	String getThing() {
		return thing;
	}
	/**
	 * @param thing the thing to set
	 */
	void setThing(String thing) {
		this.thing = thing;
	}
	/**
	 * @return the timestamp
	 */
	String getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the situationtemplate
	 */
	String getSituationtemplate() {
		return situationtemplate;
	}
	/**
	 * @param situationtemplate the situationtemplate to set
	 */
	void setSituationtemplate(String situationtemplate) {
		this.situationtemplate = situationtemplate;
	}
	/**
	 * @return the occured
	 */
	boolean isOccured() {
		return occured;
	}
	/**
	 * @param occured the occured to set
	 */
	void setOccured(boolean occured) {
		this.occured = occured;
	}
	/**
	 * @return the sensorvalues
	 */
	List<String> getSensorvalues() {
		return sensorvalues;
	}
	/**
	 * @param sensorvalues the sensorvalues to set
	 */
	void setSensorvalues(List<String> sensorvalues) {
		this.sensorvalues = sensorvalues;
	}


}

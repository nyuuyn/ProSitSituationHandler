package situationManagement;

import java.util.List;

import situationHandling.storage.datatypes.Situation;

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
	public SituationResult() {

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
	public String get_id() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(String _id) {
		this._id = _id;
	}

	/**
	 * @return the _rev
	 */
	public String get_rev() {
		return _rev;
	}

	/**
	 * @param _rev the _rev to set
	 */
	public void set_rev(String _rev) {
		this._rev = _rev;
	}

	/**
	 * @return the thing
	 */
	public String getThing() {
		return thing;
	}

	/**
	 * @param thing the thing to set
	 */
	public void setThing(String thing) {
		this.thing = thing;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the situationtemplate
	 */
	public String getSituationtemplate() {
		return situationtemplate;
	}

	/**
	 * @param situationtemplate the situationtemplate to set
	 */
	public void setSituationtemplate(String situationtemplate) {
		this.situationtemplate = situationtemplate;
	}

	/**
	 * @return the occured
	 */
	public boolean isOccured() {
		return occured;
	}

	/**
	 * @param occured the occured to set
	 */
	public void setOccured(boolean occured) {
		this.occured = occured;
	}

	/**
	 * @return the sensorvalues
	 */
	public List<String> getSensorvalues() {
		return sensorvalues;
	}

	/**
	 * @param sensorvalues the sensorvalues to set
	 */
	public void setSensorvalues(List<String> sensorvalues) {
		this.sensorvalues = sensorvalues;
	}
	
	public Situation getSituation(){
		return new Situation(situationtemplate, thing);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SituationResult [_id=" + _id + ", _rev=" + _rev + ", thing="
				+ thing + ", timestamp=" + timestamp + ", situationtemplate="
				+ situationtemplate + ", occured=" + occured
				+ ", sensorvalues=" + sensorvalues + "]";
	}
	
	


}

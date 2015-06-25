package situationHandling.storage.datatypes;

import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "actions")
public class Action {

	private int id;
	private String pluginID;
	private String address;
	private String message;

	private int ruleID;

	private HashMap<String, String> params;

	public Action() {
		super();
	}

	public Action(String pluginID, String address, String message,
			HashMap<String, String> params) {
		this.pluginID = pluginID;
		this.address = address;
		this.message = message;
		this.params = params;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPluginID() {
		return pluginID;
	}

	public void setPluginID(String pluginID) {
		this.pluginID = pluginID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getRuleID() {
		return ruleID;
	}

	public void setRuleID(int ruleID) {
		this.ruleID = ruleID;
	}

	public HashMap<String, String> getParams() {
		return params;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}

}

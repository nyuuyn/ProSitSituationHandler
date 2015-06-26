package situationHandling.storage.datatypes;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Table(name = "actions")
public class Action {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;

	@Column(name = "plugin_id")
	private String pluginID;

	@Column(name = "address")
	private String address;

	@Column(name = "message")
	private String message;


	@ElementCollection
	@MapKeyColumn (name = "name")
	@Column (name = "value")
	@CollectionTable(name = "parameters", joinColumns=@JoinColumn(name="action_id"))
	private Map<String, String> params = new HashMap<String, String>();

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


	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}

}

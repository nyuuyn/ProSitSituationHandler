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

/**
 * Wrapper class for an action. An action is executed when a certain situation
 * occurs. The situation is defined by a {@link Rule}. <div>
 * 
 * An Action is described by a pluginID, an address, a payload and several
 * optional parameters.<div>
 * 
 * The pluginID refers to the plugin that exectutes the action. The address
 * describes the address of the endpoint or the receiver of the message/payload. The
 * payload is the payload for the interaction (or simply the message that is sent
 * to someone). Furthermore, optional Parameters can be used by the action. A
 * single parameter is a key-value pair. See the documentation of the plugin for
 * information about optional paramters. <div> The class is conform to the java
 * bean specification.
 * 
 * @author Stefan
 * @see Rule
 */
/*
 * An instance of Action can be mapped to the table actions using JPA.
 */
@Entity
@Table(name = "actions")
public class Action {

	/** The id used in the database aka the PRIMARY KEY. */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;

	@Column(name = "plugin_id")
	private String pluginID;

	@Column(name = "address")
	private String address;

	@Column(name = "payload")
	private String payload;

	/**
	 * The hashmap is stored in the table "parameters", using the id of this
	 * action as foreign key.
	 */
	@ElementCollection
	@MapKeyColumn(name = "name")
	@Column(name = "value")
	@CollectionTable(name = "parameters", joinColumns = @JoinColumn(name = "action_id"))
	private Map<String, String> params = new HashMap<String, String>();

	/**
	 * Default Constructor. Use the setters to set the values
	 */
	public Action() {
		super();
	}

	/**
	 * Constructor that initializes the fields. See documentation of the class
	 * for the description of the parameters.
	 * 
	 * 
	 * @param pluginID
	 *            the id of the plugin to use.
	 * @param address
	 *            the endpoint.
	 * @param payload
	 *            the payload/message
	 * @param params
	 *            optional parameters
	 */
	public Action(String pluginID, String address, String payload,
			HashMap<String, String> params) {
		this.pluginID = pluginID;
		this.address = address;
		this.payload = payload;
		this.params = params;
	}

	/**
	 * Returns the id. The id uniquely identifies the action.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id. The id uniquely identifies the action. <div> This method
	 * should NOT be manually used, when it is intended to store the action in
	 * the database. In this case, the id will be generated.
	 *
	 * @param id
	 *            the new id in the database
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the id of the plugin to use
	 */
	public String getPluginID() {
		return pluginID;
	}

	/**
	 * Sets the the id of the plugin to use
	 *
	 * @param pluginID
	 *            the new plugin id
	 */
	public void setPluginID(String pluginID) {
		this.pluginID = pluginID;
	}

	/**
	 * Gets the address of the receiver.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address of the receiver.
	 *
	 * @param address
	 *            the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the payload that is sent.
	 *
	 * @return the payload to send
	 */
	public String getPayload() {
		return payload;
	}

	/**
	 * Sets the payload to send.
	 *
	 * @param payload
	 *            the new payload
	 */
	public void setPayload(String payload) {
		this.payload = payload;
	}

	/**
	 * Gets the optional params, used by the plugin.
	 *
	 * @return the params
	 */
	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * Sets the params, used by the plugin..
	 *
	 * @param params
	 *            the params
	 */
	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Action [id=" + id + ", pluginID=" + pluginID + ", address="
				+ address + ", payload=" + payload + ", params=" + params + "]";
	}

}

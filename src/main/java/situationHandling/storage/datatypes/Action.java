package situationHandling.storage.datatypes;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * optional parameters. Furthermore, an action allows to specify the moment in
 * which it is executed.<div>
 * 
 * The pluginID refers to the plugin that exectutes the action. The address
 * describes the address of the endpoint or the receiver of the message/payload.
 * The payload is the payload for the interaction (or simply the message that is
 * sent to someone). The execution time specifies, when exactly the action is
 * executed. Possibilities are:
 * <ul>
 * <li>When the situation occurs</li>
 * <li>When the situation disappears</li>
 * <li>Whe the situation changes</li>
 * </ul>
 * The situation is specified in the rule the action is associated to.
 * Furthermore, optional Parameters can be used by the action. A single
 * parameter is a key-value pair. See the documentation of the plugin for
 * information about optional paramters. <div> The class is conform to the java
 * bean specification.
 * 
 * @author Stefan
 * @see Rule
 * @see Situation
 * @see ExecutionTime
 */
/*
 * An instance of Action can be mapped to the table actions using JPA.
 */
@Entity
@Table(name = "actions")
public class Action {
	/**
	 * 
	 * Enum to specify the execution time of an action. An action can be
	 * executed in the following cases:
	 *
	 * 
	 * <ul>
	 * <li>When the situation occurs</li>
	 * <li>When the situation disappears</li>
	 * <li>Whe the situation changes</li>
	 * </ul>
	 * 
	 * @see Action
	 *
	 */
	public enum ExecutionTime {
		onSituationAppear, onSituationDisappear, onSituationChange
	}

	/** The id used in the database aka the PRIMARY KEY. */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;

	/**
	 * The id of the plugin to use.
	 */
	@Column(name = "plugin_id")
	private String pluginID;

	/**
	 * The receiver of the message/payload.
	 * 
	 */
	@Column(name = "address")
	private String address;

	/**
	 * The payload to send.
	 */
	@Column(name = "payload")
	private String payload;

	/**
	 * The point in time to execute the action.
	 */
	@Column(name = "execution_time")
	@Enumerated(EnumType.STRING)
	private ExecutionTime executionTime;

	/**
	 * The hashmap is stored in the table "parameters", using the id of this
	 * action as foreign key.
	 */
	@ElementCollection
	@MapKeyColumn(name = "name")
	@Column(name = "value")
	@CollectionTable(name = "parameters", joinColumns = @JoinColumn(name = "action_id"))
	private Map<String, String> params;

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
	 * @param executionTime
	 *            the point in time when the action should be executed.
	 */
	public Action(String pluginID, String address, String payload,
			ExecutionTime executionTime, HashMap<String, String> params) {
		this.pluginID = pluginID;
		this.address = address;
		this.payload = payload;
		this.executionTime = executionTime;
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
	 * Get the point in time when the action should be executed.
	 * 
	 * @return the executionTime
	 */
	public ExecutionTime getExecutionTime() {
		return executionTime;
	}

	/**
	 * Set the point in time when the action should be executed.
	 * 
	 * @param executionTime
	 *            the executionTime to set
	 */
	public void setExecutionTime(ExecutionTime executionTime) {
		this.executionTime = executionTime;
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

package situationHandling.storage.datatypes;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents an entry in the history. An entry can be used for executed actions
 * and workflow operations.
 * 
 * @author Stefan
 *
 */
@Entity
@Table(name = "history")
public class HistoryEntry {

	/** The id used in the database aka the PRIMARY KEY. */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;

	/**
	 * Date of creation.
	 */
	@Column(name = "timestamp")
	private Date timestamp;

	/** The situation or several situations. */
	@Column(name = "situation")
	private String situation;

	/** A description of the thing that happened. */
	@Column(name = "type_of_action")
	private String typeOfAction;

	/** The recipient when a message was sent. */
	@Column(name = "recipient")
	private String recipient;

	/** The payload that was sent to the recipent. */
	@Column(name = "payload")
	private String payload;

	/** Miscellaneous text to describe the thing that happened. */
	@Column(name = "misc")
	private String misc;

	/**
	 * Instantiates a new history entry. Does not init anything.
	 */
	public HistoryEntry() {
	}

	/**
	 * Instantiates a new history entry. Initiates the fields.
	 *
	 * @param situation
	 *            The situation or several situations.
	 * @param typeOfAction
	 *            the type of action. A description of the thing that happened.
	 * @param recipent
	 *            the recipient. The recipient when a message was sent.
	 * @param payload
	 *            the payload that was sent to the recipent.
	 * @param misc
	 *            Miscellaneous text to describe the thing that happened.
	 */
	public HistoryEntry(String situation, String typeOfAction, String recipent,
			String payload, String misc) {

		this.situation = situation;
		this.typeOfAction = typeOfAction;
		this.recipient = recipent;
		this.payload = payload;
		this.misc = misc;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp.
	 *
	 * @param timestamp
	 *            the new timestamp
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Gets the situation.
	 *
	 * @return The situation or several situations.
	 */
	public String getSituation() {
		return situation;
	}

	/**
	 * Sets the situation.
	 *
	 * @param situation
	 *            the situation or several situations.
	 */
	public void setSituation(String situation) {
		this.situation = situation;
	}

	/**
	 * Gets the type of action. A description of the thing that happened.
	 *
	 * @return the type of action
	 */
	public String getTypeOfAction() {
		return typeOfAction;
	}

	/**
	 * Sets the type of action. A description of the thing that happened.
	 *
	 * @param typeOfAction
	 *            the new type of action
	 */
	public void setTypeOfAction(String typeOfAction) {
		this.typeOfAction = typeOfAction;
	}

	/**
	 * Gets The recipient when a message was sent.
	 *
	 * @return the recipient
	 */
	public String getRecipient() {
		return recipient;
	}

	/**
	 * Sets The recipient when a message was sent.
	 *
	 * @param recipent
	 *            the new recipient
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * Gets the payload that was sent to the recipient.
	 *
	 * @return the payload
	 */
	public String getPayload() {
		return payload;
	}

	/**
	 * Sets the payload that was sent to the recipient.
	 *
	 * @param payload
	 *            the new payload
	 */
	public void setPayload(String payload) {
		this.payload = payload;
	}

	/**
	 * Gets the miscellaneous text to describe the thing that happened.
	 *
	 * @return the misc
	 */
	public String getMisc() {
		return misc;
	}

	/**
	 * Sets the miscellaneous text to describe the thing that happened.
	 *
	 * @param misc
	 *            the new misc
	 */
	public void setMisc(String misc) {
		this.misc = misc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// generated
		return "HistoryEntry [id=" + id + ", timestamp=" + timestamp
				+ ", situation=" + situation + ", typeOfAction=" + typeOfAction
				+ ", recipent=" + recipient + ", payload=" + payload
				+ ", misc=" + misc + "]";
	}

}

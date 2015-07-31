
package situationHandling.storage.datatypes;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TODO
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
	
	@Column (name = "timestamp")
	private Date timestamp;
	
	@Column (name = "situation")
	private String situation;

	@Column (name = "type_of_action")
	private String typeOfAction;
	
	@Column (name = "recipent")
	private String recipent;
	
	@Column (name = "payload")
	private String payload;
	
	@Column (name = "misc")
	private String misc;
	
	
//	@PrePersist
//	  protected void onCreate() {
//		timestamp = new Date();
//	  }
	


	/**
	 * 
	 */
	public HistoryEntry() {
	}




	/**

	 * @param situation
	 * @param typeOfAction
	 * @param recipent
	 * @param payload
	 * @param misc
	 */
	public HistoryEntry(String situation, String typeOfAction, String recipent, String payload,
			String misc) {

		this.situation = situation;
		this.typeOfAction = typeOfAction;
		this.recipent = recipent;
		this.payload = payload;
		this.misc = misc;
	}




	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Date getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


	public String getSituation() {
		return situation;
	}


	public void setSituation(String situation) {
		this.situation = situation;
	}


	public String getTypeOfAction() {
		return typeOfAction;
	}


	public void setTypeOfAction(String typeOfAction) {
		this.typeOfAction = typeOfAction;
	}


	public String getRecipent() {
		return recipent;
	}


	public void setRecipent(String recipent) {
		this.recipent = recipent;
	}


	public String getPayload() {
		return payload;
	}


	public void setPayload(String payload) {
		this.payload = payload;
	}


	public String getMisc() {
		return misc;
	}


	public void setMisc(String misc) {
		this.misc = misc;
	}
	
	

}

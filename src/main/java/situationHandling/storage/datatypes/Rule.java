package situationHandling.storage.datatypes;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rules")
public class Rule implements Serializable {
	

	/**
	 * Generated..
	 */
	private static final long serialVersionUID = -7865851304591856361L;

	
	//Das mit dem Composite Key hier nochmal überdenken...--> vielleicht kann man auch einfach einen combined Key als Foreign key nehmen?
	@Id
	@Column(name = "situation_name")
	private String situationName;
	
	@Id
	@Column(name = "object_name")
	private String objectName;
	
	@GeneratedValue
	private int id;

	public Rule() {
		super();
	}


	public Rule(String situationName, String objectName) {
		this.situationName = situationName;
		this.objectName = objectName;
	}
	
	public Rule (Situation situation){
		this.situationName = situation.getSituationName();
		this.objectName = situation.getObjectName();
	}



	public String getSituationName() {
		return situationName;
	}

	public void setSituationName(String situationName) {
		this.situationName = situationName;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

}

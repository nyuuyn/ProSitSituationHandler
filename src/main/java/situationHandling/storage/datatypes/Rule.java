package situationHandling.storage.datatypes;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "rules")
public class Rule {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;


	@Column(name = "situation_name")
	private String situationName;

	@Column(name = "object_name")
	private String objectName;


	@OneToMany (cascade=CascadeType.ALL)
	@JoinColumn(name = "rule_id")
	private List<Action> actions = new LinkedList<>();

	public Rule() {
		super();
	}

	public Rule(String situationName, String objectName) {
		this.situationName = situationName;
		this.objectName = objectName;
	}

	public Rule(String situationName, String objectName,
			LinkedList<Action> actions) {
		this.situationName = situationName;
		this.objectName = objectName;
		this.actions = actions;
	}

	public Rule(Situation situation) {
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

	public Situation getSituation() {
		return new Situation(situationName, objectName);
	}

	public void setSituation(Situation situation) {
		this.situationName = situation.getSituationName();
		this.objectName = situation.getObjectName();
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
	
	public void addAction (Action action){
		this.actions.add(action);
	}

}

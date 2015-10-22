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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class Rule is used as wrapper class for rules that describe the action
 * when for example a new situation is reported to the situation handler. A rule
 * describes the situation in which the rule is used and a set of actions that
 * are to be executed when the rule is used. <div> Therefore, a rule consists of
 * a single situation and several actions that should be executed when this
 * situation occurs. A Situation is described by the situation name and an
 * object id. <div> The situation is represented by {@link Situation} and one
 * or more {@link Action}s. <div> The situation uniquely identifies a rule, i.e.
 * there cannot be two rules with the same situation. The situation can be used
 * to look up a rule.
 * 
 * 
 * @author Stefan
 * @see Situation
 * @see Action
 * 
 */
/*
 * An instance of Rule can be mapped to the table rules using JPA.
 */
@Entity
@Table(name = "rules")
public class Rule {

	/** The id of the rule. Used as primary key in the database. */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;

	/** The situation name. */
	@Column(name = "situation_name")
	private String situationName;

	/** The object id. */
	@Column(name = "object_id")
	private String objectId;

	/**
	 * The actions. The actions are mapped to the table actions, using the id of
	 * this rule as foreign key.
	 * 
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "rule_id")
	private List<Action> actions = new LinkedList<>();

	/**
	 * Instantiates a new rule. The default constructor
	 */
	public Rule() {
		super();
	}

	/**
	 * Instantiates a new rule. Specifies the situation by the situation name
	 * and the object id. No actions are specified in this case. Actions can
	 * be added using the method {@link #setActions(List)} or
	 * {@link #addAction(Action)}.
	 *
	 * @param situationName
	 *            the situation name
	 * @param objectId
	 *            the object id
	 */
	public Rule(String situationName, String objectId) {
		this.situationName = situationName;
		this.objectId = objectId;
	}

	/**
	 * Instantiates a new rule using a situation object and some actions. This
	 * will create a rule: situation --> actions.
	 * 
	 * @param situation
	 *            the situation
	 * @param actions
	 *            the actions
	 */
	public Rule(Situation situation, List<Action> actions) {
		this.situationName = situation.getSituationName();
		this.objectId = situation.getObjectId();
		this.actions = actions;
	}

	/**
	 * Instantiates a new rule using a situation object. No actions are
	 * specified in this case. Actions can be added using the method
	 * {@link #setActions(List)} or {@link #addAction(Action)}.
	 *
	 * @param situation
	 *            the situation
	 */
	public Rule(Situation situation) {
		this.situationName = situation.getSituationName();
		this.objectId = situation.getObjectId();
	}

	/**
	 * Gets the situation name. The situation name specifies a situation
	 * together with the object id.<div>
	 * 
	 * The situation uniquely identifies a rule, i.e. there cannot be two rules
	 * with the same situation. The situation can be used to look up a rule.
	 *
	 * @return the situation name
	 */
	public String getSituationName() {
		return situationName;
	}

	/**
	 * Sets the situation name. The situation name specifies a situation
	 * together with the object id.<div>
	 * 
	 * The situation uniquely identifies a rule, i.e. there cannot be two rules
	 * with the same situation. The situation can be used to look up a rule.
	 *
	 * @param situationName
	 *            the new situation name
	 */
	public void setSituationName(String situationName) {
		this.situationName = situationName;
	}

	/**
	 * Gets the object id. The object id specifies a situation together with
	 * the situation name.<div>
	 * 
	 * The situation uniquely identifies a rule, i.e. there cannot be two rules
	 * with the same situation. The situation can be used to look up a rule.
	 *
	 * @return the object id
	 */
	public String getobjectId() {
		return objectId;
	}

	/**
	 * Sets the object id. The object id specifies a situation together with
	 * the situation name.<div>
	 * 
	 * The situation uniquely identifies a rule, i.e. there cannot be two rules
	 * with the same situation. The situation can be used to look up a rule.
	 *
	 * @param objectId
	 *            the new object id
	 */
	public void setobjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * Gets the id. The id uniquely identifies the rule.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id. The id uniquely identifies the rule. Don't set the id
	 * manually when creating a new rule. The creation of the id happens
	 * manually.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the situation. The situation uniquely identifies a rule, i.e. there
	 * cannot be two rules with the same situation. The situation can be used to
	 * look up a rule.
	 *
	 * @return the situation
	 */
	@JsonIgnore
	public Situation getSituation() {
		return new Situation(situationName, objectId);
	}

	/**
	 * Sets the situation. The situation uniquely identifies a rule, i.e. there
	 * cannot be two rules with the same situation. The situation can be used to
	 * look up a rule.
	 *
	 * @param situation
	 *            the new situation
	 */
	@JsonIgnore
	public void setSituation(Situation situation) {
		this.situationName = situation.getSituationName();
		this.objectId = situation.getObjectId();
	}

	/**
	 * Gets the actions. The actions stored in this rule are executed when the
	 * specified situation occurs.
	 *
	 * @return the actions
	 */
	public List<Action> getActions() {
		return actions;
	}

	/**
	 * Sets the actions. Note that by using this method, the current list of
	 * actions is overwritten. <div> The actions stored in this rule are
	 * executed when the specified situation occurs.
	 *
	 * @param actions
	 *            the new actions
	 */
	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	/**
	 * Adds the action. The action is appended to the current list of actions.
	 * <div> The actions stored in this rule are executed when the specified
	 * situation occurs.
	 * 
	 * @param action
	 *            the action
	 */
	public void addAction(Action action) {
		this.actions.add(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("---------------------------------------------%n");
		sb.append("---------------------------------------------%n");
		sb.append("Rule: [id=" + id + ", situationName=" + situationName
				+ ", objectId=" + objectId + ", actions: ");

		for (Action action : actions) {
			sb.append("%n-------------------------------------%n");
			sb.append(action.toString());
		}
		sb.append("%n---------------------------------------------");
		return String.format(sb.toString());
	}

}

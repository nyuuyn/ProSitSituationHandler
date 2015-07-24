package situationHandling.storage.datatypes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * 
 * A HandledSituation is a situation that is handled by an endpoint. <br>
 * A handled situation posses all attributes of an {@link Situation} (name and
 * object). Furthermore a HandledSituation states wheter the situation must hold
 * or not. A HandledSituation can also optional, meaning that the endpoint can
 * be used even if the situation holds (or not). It can be stated that a
 * rollback action has to be initiated by the endpoint if the Handled Situation
 * changes.
 * 
 * 
 * @see Situation
 * @see Endpoint
 * 
 * @author Stefan
 *
 */
@Entity
@Table(name = "handled_situations")
public class HandledSituation {

	/**
	 * The handled situation's id. Used as primary key in the database. Never
	 * set this manually. The database will generate this value.
	 */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;

	/** The situation name. */
	@Column(name = "situation_name")
	private String situationName;

	/** The object name. */
	@Column(name = "object_name")
	private String objectName;

	/**
	 * Describes the usage of the situation. When true, the situation occured,
	 * when false not.
	 */
	@Column(name = "situation_holds")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean situationHolds;

	/**
	 * States whether the handled situation is optional to be handled or not.
	 */
	@Column(name = "optional")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean optional;

	/**
	 * States whether a rollback action has to be done, if the situation
	 * changes.
	 */
	@Column(name = "rollback_on_change")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean rollbackOnChange;

	/**
	 * Default constructor. Does not initialize anything. Use the setters.
	 */
	public HandledSituation() {
	}

	/**
	 * @param id
	 *            The handled situation's id. Used as primary key in the
	 *            database. Never set this manually. The database will generate
	 *            this value.
	 * @param situationName
	 *            The situation name
	 * @param objectName
	 *            The object name.
	 * @param situationHolds
	 *            Describes the usage of the situation. When true, the situation
	 *            occured, when false not.
	 * @param optional
	 *            States whether the handled situation is optional to be handled
	 *            or not.
	 * @param rollbackOnChange
	 *            States whether a rollback action has to be done, if the
	 *            situation changes.
	 */
	public HandledSituation(int id, String situationName, String objectName,
			boolean situationHolds, boolean optional, boolean rollbackOnChange) {
		this.id = id;
		this.situationName = situationName;
		this.objectName = objectName;
		this.situationHolds = situationHolds;
		this.optional = optional;
		this.rollbackOnChange = rollbackOnChange;
	}

	/**
	 * @return The handled situation's id. Used as primary key in the database.
	 *         Never set this manually. The database will generate this value.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            The handled situation's id. Used as primary key in the
	 *            database. Never set this manually. The database will generate
	 *            this value.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the situationName
	 */
	public String getSituationName() {
		return situationName;
	}

	/**
	 * @param situationName
	 *            the situationName to set
	 */
	public void setSituationName(String situationName) {
		this.situationName = situationName;
	}

	/**
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName
	 *            the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * Describes the usage of the situation. When true, the situation occured,
	 * when false not.
	 * 
	 * @return situationHolds
	 */
	public Boolean isSituationHolds() {
		return situationHolds;
	}

	/**
	 * 
	 * Describes the usage of the situation. When true, the situation occured,
	 * when false not.
	 * 
	 * @param situationHolds
	 */
	public void setSituationHolds(Boolean situationHolds) {
		this.situationHolds = situationHolds;
	}

	/**
	 * 
	 * States whether the handled situation is optional to be handled or not.
	 * 
	 * @return
	 */
	public Boolean isOptional() {
		return optional;
	}

	/**
	 * States whether the handled situation is optional to be handled or not.
	 * 
	 * @param optional
	 */
	public void setOptional(Boolean optional) {
		this.optional = optional;
	}

	/**
	 * States whether a rollback action has to be done, if the situation
	 * changes.
	 * 
	 * @return
	 */
	public Boolean isRollbackOnChange() {
		return rollbackOnChange;
	}

	/**
	 * States whether a rollback action has to be done, if the situation
	 * changes.
	 * 
	 * @param rollbackOnChange
	 */
	public void setRollbackOnChange(Boolean rollbackOnChange) {
		this.rollbackOnChange = rollbackOnChange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String holds = situationHolds ? "situation holds"
				: "situation does not hold";
		String optionalString = optional ? "optional" : "required";
		String rollback = rollbackOnChange ? "rollback" : "no rollback";
		return "[" + situationName + " | " + objectName + " | " + holds + " | "
				+ optionalString + " | " + rollback + "]";
	}
}

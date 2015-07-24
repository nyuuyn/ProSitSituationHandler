package situationHandling.storage.datatypes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * TODO: Javadoc
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

	@Column(name = "situation_holds")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean situationHolds;

	@Column(name = "optional")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean optional;

	@Column(name = "rollback_on_change")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean rollbackOnChange;

	public HandledSituation() {
	}

	/**
	 * @param id
	 * @param situationName
	 * @param objectName
	 * @param situationHolds
	 * @param optional
	 * @param rollbackOnChange
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
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
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
	 * @return the situationHolds
	 */
	public boolean isSituationHolds() {
		return situationHolds;
	}

	/**
	 * @param situationHolds
	 *            the situationHolds to set
	 */
	public void setSituationHolds(boolean situationHolds) {
		this.situationHolds = situationHolds;
	}

	/**
	 * @return the optional
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * @param optional
	 *            the optional to set
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	/**
	 * @return the rollbackOnChange
	 */
	public boolean isRollbackOnChange() {
		return rollbackOnChange;
	}

	/**
	 * @param rollbackOnChange
	 *            the rollbackOnChange to set
	 */
	public void setRollbackOnChange(boolean rollbackOnChange) {
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

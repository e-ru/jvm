package eu.rudisch.users.persistance.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the memberships database table.
 * 
 */
@Entity
@Table(name="memberships")
@NamedQuery(name="Membership.findAll", query="SELECT m FROM Membership m")
public class Membership implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="accounts_id")
	private int accountsId;

	private int id;

	@Column(name="roles_id")
	private int rolesId;

	@Column(name="users_id")
	private int usersId;

	public Membership() {
	}

	public int getAccountsId() {
		return this.accountsId;
	}

	public void setAccountsId(int accountsId) {
		this.accountsId = accountsId;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRolesId() {
		return this.rolesId;
	}

	public void setRolesId(int rolesId) {
		this.rolesId = rolesId;
	}

	public int getUsersId() {
		return this.usersId;
	}

	public void setUsersId(int usersId) {
		this.usersId = usersId;
	}

}
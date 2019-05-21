package eu.rudisch.users.persistance.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the logins database table.
 * 
 */
@Entity
@Table(name="logins")
@NamedQuery(name="Login.findAll", query="SELECT l FROM Login l")
public class Login implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;

	@Column(name="password_hash")
	private String passwordHash;

	@Column(name="password_salt")
	private String passwordSalt;

	@Column(name="user_name")
	private String userName;

	@Column(name="users_id")
	private int usersId;

	public Login() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPasswordHash() {
		return this.passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getPasswordSalt() {
		return this.passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUsersId() {
		return this.usersId;
	}

	public void setUsersId(int usersId) {
		this.usersId = usersId;
	}

}
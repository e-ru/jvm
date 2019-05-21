package eu.rudisch.users.persistance.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the login database table.
 * 
 */
@Entity
@Table(name = "login")
@NamedQuery(name = "Login.findAll", query = "SELECT l FROM Login l")
public class Login implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_name", unique = true)
	private String userName;

	@Column(name = "password_hash")
	private String passwordHash;

	@Column(name = "password_salt")
	private String passwordSalt;

	// bi-directional many-to-one association to UserDetail
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
//	@Column(nullable = false)
	private UserDetail userDetail;

	public Login() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public UserDetail getUserDetail() {
		return this.userDetail;
	}

	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
	}

	@Override
	public String toString() {
		return "Login [id=" + id + ", userName=" + userName + ", passwordHash=" + passwordHash + ", passwordSalt="
				+ passwordSalt + ", userDetail=" + userDetail + "]";
	}
}
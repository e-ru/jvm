package eu.rudisch.users.persistance.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import eu.rudisch.users.rest.model.User;

/**
 * The persistent class for the user_details database table.
 * 
 */
@Entity
@Table(name = "user_details")
@NamedQuery(name = "UserDetail.findAll", query = "SELECT u FROM UserDetail u")
public class UserDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	// TODO add unique email field

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	// bi-directional one-to-one association to Login
	@OneToOne(mappedBy = "userDetail", cascade = CascadeType.ALL)
	private Login login;

	// bi-directional one-to-one association to Membership
	@OneToOne(mappedBy = "userDetail", cascade = CascadeType.ALL)
	private Membership membership;

	public UserDetail() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Login getLogin() {
		return this.login;
	}

	public void setLogin(Login login) {
		this.login = login;
		login.setUserDetail(this);
	}

	public Membership getMembership() {
		return this.membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
		membership.setUserDetail(this);
	}

	@Override
	public String toString() {
		return "UserDetail [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", login=" + login
				+ ", membership=" + membership + "]";
	}

	public static UserDetail fromUser(User user) {
		UserDetail userDetail = new UserDetail();
		Membership membership = new Membership();
		if (user.getAccounts() != null)
			user.getAccounts().stream()
					.map(a -> Account.fromRestAccount(a))
					.forEach(a -> membership.addAccount(a));
		if (user.getRoles() != null)
			user.getRoles().stream()
					.map(r -> Role.fromParameter(r))
					.forEach(r -> membership.addRole(r));
		userDetail.setMembership(membership);
		return userDetail;
	}

}
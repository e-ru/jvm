package eu.rudisch.users.persistance.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the user_details database table.
 * 
 */
@Entity
@Table(name = "user_details")
@NamedQuery(name = "UserDetail.findAll", query = "SELECT u FROM UserDetail u")
public class UserDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	// bi-directional many-to-one association to Login
	@OneToMany(mappedBy = "userDetail", cascade = CascadeType.ALL)
	private List<Login> logins = new ArrayList<>();;

	// bi-directional many-to-one association to Membership
	@OneToMany(mappedBy = "userDetail", cascade = CascadeType.ALL)
	private List<Membership> memberships = new ArrayList<>();;

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

	public List<Login> getLogins() {
		return this.logins;
	}

	public void setLogins(List<Login> logins) {
		this.logins = logins;
	}

	public Login addLogin(Login login) {
		getLogins().add(login);
		login.setUserDetail(this);

		return login;
	}

	public Login removeLogin(Login login) {
		getLogins().remove(login);
		login.setUserDetail(null);

		return login;
	}

	public List<Membership> getMemberships() {
		return this.memberships;
	}

	public void setMemberships(List<Membership> memberships) {
		this.memberships = memberships;
	}

	public Membership addMembership(Membership membership) {
		getMemberships().add(membership);
		membership.setUserDetail(this);

		return membership;
	}

	public Membership removeMembership(Membership membership) {
		getMemberships().remove(membership);
		membership.setUserDetail(null);

		return membership;
	}

	@Override
	public String toString() {
		return "UserDetail [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", logins=" + logins
				+ ", memberships=" + memberships + "]";
	}

}
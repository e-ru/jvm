package eu.rudisch.users.persistance.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The persistent class for the account database table.
 * 
 */
@Entity
@Table(name = "account")
@NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a")
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "email_address")
	private String emailAddress;

	@Column(name = "phone_number")
	private String phoneNumber;

	// bi-directional one-to-one association to Membership
	@OneToOne(mappedBy = "account")
	private Membership membership;

	public Account() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmailAddress() {
		return this.emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Membership getMembership() {
		return this.membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", emailAddress=" + emailAddress + ", phoneNumber=" + phoneNumber + ", membership="
				+ membership + "]";
	}

}
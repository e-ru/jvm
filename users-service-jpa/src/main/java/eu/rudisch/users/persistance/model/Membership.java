package eu.rudisch.users.persistance.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The persistent class for the membership database table.
 * 
 */
@Entity
@Table(name = "membership")
@NamedQuery(name = "Membership.findAll", query = "SELECT m FROM Membership m")
public class Membership implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "account_emailaddress")
	private String accountEmailAddress;

	@Column(name = "account_phonenumber")
	private String accountPhoneNumber;

	// bi-directional one-to-one association to UserDetail
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
//	@Column(nullable = false)
	private UserDetail userDetail;

	// bi-directional many-to-many association to Account
	@ManyToMany(cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
	})
	@JoinTable(name = "membership_account", joinColumns = @JoinColumn(name = "membership_id"), inverseJoinColumns = @JoinColumn(name = "account_id"))
	private Set<Account> accounts = new HashSet<>();

	// bi-directional many-to-many association to Role
	@ManyToMany(cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
	})
	@JoinTable(name = "membership_role", joinColumns = @JoinColumn(name = "membership_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	public Membership() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccountEmailAddress() {
		return accountEmailAddress;
	}

	public void setAccountEmailAddress(String accountEmailAddress) {
		this.accountEmailAddress = accountEmailAddress;
	}

	public String getAccountPphoneNumber() {
		return accountPhoneNumber;
	}

	public void setAccountPhoneNumber(String accountPhoneNumber) {
		this.accountPhoneNumber = accountPhoneNumber;
	}

	public UserDetail getUserDetail() {
		return this.userDetail;
	}

	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
	}

	public Set<Account> getAccounts() {
		return this.accounts;
	}

	public void setAccount(Set<Account> accounts) {
		this.accounts = accounts;
	}

	public Account addAccount(Account account) {
		getAccounts().add(account);
		account.addMembership(this);

		return account;
	}

	public Account removeAccount(Account account) {
		getAccounts().remove(account);
		account.removeMembership(null);

		return account;
	}

	public Set<Role> getRoles() {
		return this.roles;
	}

	public void setRole(Set<Role> roles) {
		this.roles = roles;
	}

	public Role addRole(Role role) {
		getRoles().add(role);
		role.addMembership(this);

		return role;
	}

	public Role removeRole(Role role) {
		getRoles().remove(role);
		role.removeMembership(null);

		return role;
	}

	@Override
	public String toString() {
		return "Membership [id=" + id + ", accountEmailAddress=" + accountEmailAddress + ", accountPphoneNumber="
				+ accountPhoneNumber + "]";
	}

}
package eu.rudisch.authorizationserver.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	public User() {
	}

	public User(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.enabled = user.isEnabled();
		this.accountExpired = user.isAccountExpired();
		this.credentialsExpired = user.isCredentialsExpired();
		this.accountLocked = user.isAccountLocked();
		this.roles = user.getRoles();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "username")
	private String username;
	@Column(name = "password")
	private String password;
	@Column(name = "email")
	private String email;
	@Column(name = "enabled")
	private boolean enabled;
	@Column(name = "accountExpired")
	private boolean accountExpired;
	@Column(name = "credentialsExpired")
	private boolean credentialsExpired;
	@Column(name = "accountLocked")
	private boolean accountLocked;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "role_user", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "role_id", referencedColumnName = "id") })
	private List<Role> roles;

}

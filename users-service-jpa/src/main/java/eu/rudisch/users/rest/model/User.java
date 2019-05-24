package eu.rudisch.users.rest.model;

import java.util.Set;
import java.util.stream.Collectors;

import eu.rudisch.users.persistance.model.UserDetail;

public final class User {

	private String firstName = null;
	private String lastName = null;

	private Set<Account> accounts = null;
	private Set<String> roles = null;

	private User() {
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Set<Account> getAccounts() {
		return accounts;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public static User fromParameter(String firstName, String lastName, Set<Account> accounts, Set<String> roles) {
		User user = new User();
		user.firstName = firstName;
		user.lastName = lastName;
		user.accounts = accounts;
		user.roles = roles;
		return user;
	}

	public static User fromUserDetail(UserDetail userDetail) {
		Set<Account> accounts = userDetail.getMembership().getAccounts().stream()
				.map(accountEntity -> Account.fromParameter(accountEntity.getName(),
						userDetail.getMembership().getAccountEmailAddress(),
						userDetail.getMembership().getAccountPphoneNumber()))
				.collect(Collectors.toSet());
		Set<String> roles = userDetail.getMembership().getRoles().stream()
				.map(roleEntity -> roleEntity.getRole())
				.collect(Collectors.toSet());
		return User.fromParameter(userDetail.getFirstName(), userDetail.getLastName(), accounts, roles);
	}
}

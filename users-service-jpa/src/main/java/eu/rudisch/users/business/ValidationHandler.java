package eu.rudisch.users.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Role;

public class ValidationHandler {

	HashSet<String> invalidAccounts = new HashSet<>();
	HashSet<String> invalidRoles = new HashSet<>();

	public HashSet<String> getInvalidAccounts() {
		return invalidAccounts;
	}

	public HashSet<String> getInvalidRoles() {
		return invalidRoles;
	}

	public boolean validateAccounts(List<Account> persAccounts, Set<eu.rudisch.users.rest.model.AccountRep> restAccounts) {
		Set<String> accounts = persAccounts.stream()
				.map(a -> a.getName())
				.collect(Collectors.toSet());
		invalidAccounts = new HashSet<>(restAccounts.stream()
				.map(a -> a.getAccountName())
				.collect(Collectors.toSet()));
		return validate(accounts, invalidAccounts);
	}

	public boolean validateRoles(List<Role> persRoles, Set<String> restRoles) {
		Set<String> roles = persRoles.stream()
				.map(a -> a.getRole())
				.collect(Collectors.toSet());
		invalidRoles = new HashSet<>(restRoles);
		return validate(roles, invalidRoles);
	}

	boolean validate(Set<String> valid, HashSet<String> toProve) {
		toProve.removeAll(valid);
		return toProve.isEmpty();
	}

}

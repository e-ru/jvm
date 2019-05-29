package eu.rudisch.users.rest.model;

import java.util.Set;

public final class Error {

	private String message = null;
	private String invalidAccounts = null;
	private String invalidRoles = null;

	private Error() {
	}

	public String getMessage() {
		return message;
	}

	public String getInvalidAccounts() {
		return invalidAccounts;
	}

	public String getInvalidRoles() {
		return invalidRoles;
	}

	public static Error fromParameter(String message, Set<String> invalidAccounts, Set<String> invalidRoles) {
		Error e = new Error();
		e.message = message;
		e.invalidAccounts = invalidAccounts.isEmpty() ? "" : String.join(", ", invalidAccounts);
		e.invalidRoles = invalidRoles.isEmpty() ? "" : String.join(", ", invalidRoles);
		return e;
	}
}

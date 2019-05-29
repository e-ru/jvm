package eu.rudisch.users.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Role;

class ValidationHandlerTest {

	@Test
	void shouldValidateAccountsTrue() {
		ValidationHandler validationHandler = new ValidationHandler();
		Account a1 = new Account();
		a1.setName("internal");
		Account a2 = new Account();
		a2.setName("external");
		eu.rudisch.users.rest.model.Account a3 = eu.rudisch.users.rest.model.Account.fromParameter("external");

		List<Account> persAccounts = List.of(a1, a2);
		Set<eu.rudisch.users.rest.model.Account> restAccounts = Set.of(a3);
		boolean valid = validationHandler.validateAccounts(persAccounts, restAccounts);

		assertTrue(valid);
		assertEquals(0, validationHandler.getInvalidAccounts().size());
	}

	@Test
	void shouldValidateAccountsFalse() {
		ValidationHandler validationHandler = new ValidationHandler();
		Account a1 = new Account();
		a1.setName("internal");
		Account a2 = new Account();
		a2.setName("external");
		eu.rudisch.users.rest.model.Account a3 = eu.rudisch.users.rest.model.Account.fromParameter("wrongAcount");

		List<Account> persAccounts = List.of(a1, a2);
		Set<eu.rudisch.users.rest.model.Account> restAccounts = Set.of(a3);
		boolean valid = validationHandler.validateAccounts(persAccounts, restAccounts);

		assertFalse(valid);
		assertEquals(1, validationHandler.getInvalidAccounts().size());
	}

	@Test
	void shouldValidateRolesTrue() {
		ValidationHandler validationHandler = new ValidationHandler();
		Role r1 = new Role();
		r1.setRole("member");
		Role r2 = new Role();
		r2.setRole("admin");
		String r3 = "member";

		List<Role> persRoles = List.of(r1, r2);
		Set<String> restRoles = Set.of(r3);
		boolean valid = validationHandler.validateRoles(persRoles, restRoles);
		assertTrue(valid);
		assertEquals(0, validationHandler.getInvalidRoles().size());
	}

	@Test
	void shouldValidateRolesFalse() {
		ValidationHandler validationHandler = new ValidationHandler();
		Role r1 = new Role();
		r1.setRole("member");
		Role r2 = new Role();
		r2.setRole("admin");
		String r3 = "wrongRole";

		List<Role> persRoles = List.of(r1, r2);
		Set<String> restRoles = Set.of(r3);
		boolean valid = validationHandler.validateRoles(persRoles, restRoles);
		assertFalse(valid);
		assertEquals(1, validationHandler.getInvalidRoles().size());
	}

	@Test
	void shouldValidateTrue() {
		ValidationHandler validationHandler = new ValidationHandler();

		Set<String> valids = Set.of("foo", "bar");
		HashSet<String> toProve = new HashSet<String>(Set.of("bar"));

		boolean valid = validationHandler.validate(valids, toProve);
		assertTrue(valid);
		assertEquals(0, toProve.size());
	}

	@Test
	void shouldValidateFalse() {
		ValidationHandler validationHandler = new ValidationHandler();

		Set<String> valids = Set.of("foo", "bar");
		HashSet<String> toProve = new HashSet<String>(Set.of("foobar"));

		boolean valid = validationHandler.validate(valids, toProve);
		assertFalse(valid);
		assertEquals(1, toProve.size());
	}

}

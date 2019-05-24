package eu.rudisch.users;

import eu.rudisch.users.business.HashHandler;
import eu.rudisch.users.business.HashHandler.HashType;
import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Login;
import eu.rudisch.users.persistance.model.Membership;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

public final class TestUtils {

	private TestUtils() {
	}

	public static UserDetail creatUserDetail(String userName, String emailAddress, Account account, Role role) {
		HashHandler hashHandler = new HashHandler();

		Membership membership = new Membership();
		membership.setAccountEmailAddress(emailAddress);
		membership.setAccountPhoneNumber("0123455");
		membership.addAccount(account);
		membership.addRole(role);

		Login login = new Login();
		login.setUserName(userName);
		login.setPasswordHash(hashHandler.generateHash("test", HashType.SHA256));
		login.setPasswordSalt("1243");

		UserDetail userDetail = new UserDetail();
		userDetail.setFirstName("Bob");
		userDetail.setLastName("Smith");

		userDetail.setLogin(login);
		userDetail.setMembership(membership);

		return userDetail;
	}
}

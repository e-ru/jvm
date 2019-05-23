package eu.rudisch.users.persistance;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.persistence.PersistenceException;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import eu.rudisch.users.Utils;
import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Login;
import eu.rudisch.users.persistance.model.Membership;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

@TestMethodOrder(OrderAnnotation.class)
class SqlServiceIntegrationTest {

	// use lombok in entities?

	UserDetail creatUserDetail(String userName, String emailAddress) {
		Role role = new Role();
		role.setRole("member");

		Account account = new Account();
//		account.setEmailAddress(emailAddress);
//		account.setPhoneNumber("0123455");

		Membership membership = new Membership();
		membership.addAccount(account);
		membership.addRole(role);

		Login login = new Login();
		login.setUserName(userName);
		login.setPasswordHash(Utils.getHash("test"));
		login.setPasswordSalt("1243");

		UserDetail userDetail = new UserDetail();
		userDetail.setFirstName("Bob");
		userDetail.setLastName("Smith");

		userDetail.setLogin(login);
		userDetail.setMembership(membership);

		return userDetail;
	}

	@Test
	@Order(1)
	void shouldInsertTwoUsers() {
		SqlService sqlService = new SqlService("users-service-jpa-int");

		int id = 0;
		UserDetail userDetail = creatUserDetail("bos", "bob@smith.com");
		id = sqlService.insertUserDetail(userDetail);
		assertNotEquals(0, id);

		id = 0;
		userDetail = creatUserDetail("bos2", "bob@smith.com");
		id = sqlService.insertUserDetail(userDetail);
		assertNotEquals(0, id);
	}

	@Test
	@Order(2)
	void shouldNotInsertSameUserTwice() {
		SqlService sqlService = new SqlService("users-service-jpa-int");

		int id = 0;
		UserDetail userDetail = creatUserDetail("bos3", "bob@smith.com");
		id = sqlService.insertUserDetail(userDetail);
		assertNotEquals(0, id);

		assertThrows(PersistenceException.class, () -> {
			sqlService.insertUserDetail(userDetail);
		});

		assertThrows(PersistenceException.class, () -> {
			UserDetail userDetailDublicate = creatUserDetail("bos3", "bob@smith.com");
			sqlService.insertUserDetail(userDetailDublicate);
		});
	}

	@Test
	@Order(3)
	void shouldSelectSingleEntityById() {
		SqlService sqlService = new SqlService("users-service-jpa-int");
		UserDetail userDetail = sqlService.getUserDetailById(1);
		assertNotNull(userDetail);
	}

	@Test
	@Order(4)
	void shouldSelectSingleEntityByUserName() {
		SqlService sqlService = new SqlService("users-service-jpa-int");
		UserDetail userDetail = sqlService.getUserDetailByUserName("bos");
		assertNotNull(userDetail);
	}

	@Test
	@Order(5)
	void shouldSelectAll() {
		SqlService sqlService = new SqlService("users-service-jpa-int");
		var userDetails = sqlService.getUserDetails();
		assertEquals(3, userDetails.size());
	}

//	@Test
//	@Order(6)
//	void shouldUpdateById() {
//		SqlService sqlService = new SqlService("users-service-jpa-int");
//		UserDetail userDetail = creatUserDetail("bos", "bob_master@smith.com");
//		sqlService.updateUserDetailById(1, userDetail);
//		userDetail = sqlService.getUserDetailByUserName("bos");
//		assertEquals("bob_master@smith.com", userDetail.get);
//	}
//
//	@Test
//	@Order(7)
//	void shouldUpdateByUserName() {
//		SqlService sqlService = new SqlService("users-service-jpa-int");
//
//	}
}

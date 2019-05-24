package eu.rudisch.users.persistance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import eu.rudisch.users.TestUtils;
import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

@TestMethodOrder(OrderAnnotation.class)
class SqlServiceIntegrationTest {

	// use lombok in entities?

//	static Supplier<EntityManagerFactory> factoryCreator;
//	static FactoryCreator factoryCreator;

	@BeforeAll
	static void init() {
//		factoryCreator = new FactoryCreatorSqlIntegrationTest();
//		factoryCreator = () -> Persistence.createEntityManagerFactory("users-service-jpa-int");
		SqlService sqlService = new SqlServiceImpl("users-service-jpa-int");
//		SqlServiceImpl sqlService = new SqlServiceImpl(factoryCreator);

		Account account = new Account();
		account.setName("internal");
		sqlService.insertAccount(account);

		account = new Account();
		account.setName("external");
		sqlService.insertAccount(account);

		Role role = new Role();
		role.setRole("user");
		sqlService.insertRole(role);

		role = new Role();
		role.setRole("admin");
		sqlService.insertRole(role);

	}

	@Test
	@Order(1)
	void shouldInsertTwoUsers() {
		SqlServiceImpl sqlService = new SqlServiceImpl("users-service-jpa-int");

		Role role = sqlService.getRole("user");
		Account account = sqlService.getAccount("internal");

		int id = 0;
		UserDetail userDetail = TestUtils.creatUserDetail("bos", "bob@smith.com", account, role);
		id = sqlService.insertUserDetail(userDetail);
		assertNotEquals(0, id);

		id = 0;
		userDetail = TestUtils.creatUserDetail("bos2", "bob@smith.com", account, role);
		id = sqlService.insertUserDetail(userDetail);
		assertNotEquals(0, id);

		List<Role> roles = sqlService.getRoles();
		assertEquals(2, roles.size());
	}

	@Test
	@Order(2)
	void shouldNotInsertSameUserTwice() {
		SqlServiceImpl sqlService = new SqlServiceImpl("users-service-jpa-int");
		Role role = sqlService.getRole("user");
		Account account = sqlService.getAccount("internal");

		int id = 0;
		UserDetail userDetail = TestUtils.creatUserDetail("bos3", "bob@smith.com", account, role);
		id = sqlService.insertUserDetail(userDetail);
		assertNotEquals(0, id);

		assertThrows(PersistenceException.class, () -> {
			sqlService.insertUserDetail(userDetail);
		});

		assertThrows(PersistenceException.class, () -> {
			UserDetail userDetailDublicate = TestUtils.creatUserDetail("bos3", "bob@smith.com", account, role);
			sqlService.insertUserDetail(userDetailDublicate);
		});
	}

	@Test
	@Order(3)
	void shouldSelectSingleEntityById() {
		SqlServiceImpl sqlService = new SqlServiceImpl("users-service-jpa-int");
		UserDetail userDetail = sqlService.getUserDetailById(1);
		assertNotNull(userDetail);
	}

	@Test
	@Order(4)
	void shouldSelectSingleEntityByUserName() {
		SqlServiceImpl sqlService = new SqlServiceImpl("users-service-jpa-int");
		UserDetail userDetail = sqlService.getUserDetailByUserName("bos");
		assertNotNull(userDetail);
	}

	@Test
	@Order(5)
	void shouldSelectAll() {
		SqlServiceImpl sqlService = new SqlServiceImpl("users-service-jpa-int");
		var userDetails = sqlService.getUserDetails();
		assertEquals(3, userDetails.size());
	}

	@Test
	@Order(6)
	void shouldUpdateById() {
		SqlServiceImpl sqlService = new SqlServiceImpl("users-service-jpa-int");

		Role role = sqlService.getRole("admin");
		Account account = sqlService.getAccount("external");
		UserDetail userDetail = TestUtils.creatUserDetail("bos", "bob_master@smith.com", account, role);

		sqlService.updateUserDetailById(1, userDetail);
		userDetail = sqlService.getUserDetailByUserName("bos");

		assertEquals("bob_master@smith.com", userDetail.getMembership().getAccountEmailAddress());
		assertEquals(2, userDetail.getMembership().getAccounts().size());
		assertEquals(2, userDetail.getMembership().getRoles().size());
	}

	@Test
	@Order(7)
	void shouldUpdateByUserName() {
		SqlServiceImpl sqlService = new SqlServiceImpl("users-service-jpa-int");

		Role role = sqlService.getRole("user");
		Account account = sqlService.getAccount("external");
		UserDetail userDetail = TestUtils.creatUserDetail("bos2", "bob_master@smith.com", account, role);

		userDetail = sqlService.updateUserDetailByUserName("bos2", userDetail);
		assertEquals("bob_master@smith.com", userDetail.getMembership().getAccountEmailAddress());
		assertEquals(2, userDetail.getMembership().getAccounts().size());
		assertEquals(1, userDetail.getMembership().getRoles().size());
	}

	@Test
	@Order(8)
	void shouldDeleteById() {
		SqlServiceImpl sqlService = new SqlServiceImpl("users-service-jpa-int");

		sqlService.removeById(1);
		UserDetail userDetail = sqlService.getUserDetailById(1);
		assertNull(userDetail);
	}

	@Test
	@Order(9)
	void shouldDeleteByUserName() {
		SqlServiceImpl sqlService = new SqlServiceImpl("users-service-jpa-int");

		sqlService.removeByUserName("bos2");
		UserDetail userDetail = sqlService.getUserDetailByUserName("bos2");
		assertNull(userDetail);
	}
}

package eu.rudisch.users.persistance;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Login;
import eu.rudisch.users.persistance.model.Membership;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

@ExtendWith(MockitoExtension.class)
public class SqlServiceImplTest {

	@Mock
	GenericDao genericDao;

	SqlServiceImpl sqlServiceImpl;

	@Test
	void shouldInsert() {
		sqlServiceImpl = new SqlServiceImpl(genericDao);
		UserDetail userDetail = new UserDetail();
		Account account = new Account();
		Role role = new Role();

		when(genericDao.insert(userDetail)).thenReturn(userDetail);
		when(genericDao.insert(account)).thenReturn(account);
		when(genericDao.insert(role)).thenReturn(role);
		sqlServiceImpl.insertUserDetail(userDetail);
		sqlServiceImpl.insertAccount(account);
		sqlServiceImpl.insertRole(role);
		verify(genericDao, times(1)).insert(userDetail);
		verify(genericDao, times(1)).insert(account);
		verify(genericDao, times(1)).insert(role);
	}

	@Test
	void shouldGetUserDetailById() {
		sqlServiceImpl = new SqlServiceImpl(genericDao);
		UserDetail userDetail = new UserDetail();
		userDetail.setId(1);

		when(genericDao.find(UserDetail.class, 1)).thenReturn(userDetail);
		sqlServiceImpl.getUserDetailById(1);
		verify(genericDao, times(1)).find(UserDetail.class, 1);
	}

	@Test
	void shouldGetByName() {
		sqlServiceImpl = new SqlServiceImpl(genericDao);
		UserDetail userDetail = new UserDetail();
		Login login = new Login();
		login.setUserDetail(userDetail);
		Account account = new Account();
		Role role = new Role();

		when(genericDao.selectSingle(Login.class, "SELECT l FROM Login l WHERE l.userName=?1", "bos"))
				.thenReturn(login);
		when(genericDao.selectSingle(Account.class, "SELECT a FROM Account a WHERE a.name=?1", "internal"))
				.thenReturn(account);
		when(genericDao.selectSingle(Role.class, "SELECT r FROM Role r WHERE r.role=?1", "member"))
				.thenReturn(role);
		sqlServiceImpl.getUserDetailByUserName("bos");
		sqlServiceImpl.getRole("member");
		sqlServiceImpl.getAccount("internal");
		verify(genericDao, times(1)).selectSingle(Login.class, "SELECT l FROM Login l WHERE l.userName=?1",
				"bos");
		verify(genericDao, times(1)).selectSingle(Account.class, "SELECT a FROM Account a WHERE a.name=?1", "internal");
		verify(genericDao, times(1)).selectSingle(Role.class, "SELECT r FROM Role r WHERE r.role=?1", "member");
	}

	@Test
	void shouldGetLists() {
		sqlServiceImpl = new SqlServiceImpl(genericDao);
		when(genericDao.selectList(UserDetail.class, "SELECT u FROM UserDetail u")).thenReturn(new ArrayList<>());
		when(genericDao.selectList(Role.class, "SELECT r FROM Role r")).thenReturn(new ArrayList<>());
		sqlServiceImpl.getUserDetails();
		sqlServiceImpl.getRoles();
		verify(genericDao, times(1)).selectList(UserDetail.class, "SELECT u FROM UserDetail u");
		verify(genericDao, times(1)).selectList(Role.class, "SELECT r FROM Role r");
	}

//	@Test
// TODO: rework update methods
	void shouldUpdate() {
		sqlServiceImpl = new SqlServiceImpl(genericDao);
		int updateId = 2;
		UserDetail newValues = new UserDetail();
		newValues.setId(1);
		Membership membership = new Membership();
		membership.addAccount(new Account());
		membership.addRole(new Role());
		when(genericDao.update(() -> sqlServiceImpl.getUserDetailByUserName("bos"),
				toUpdate -> sqlServiceImpl.updateUserDetailByUserDetail(toUpdate, newValues))).thenReturn(newValues);
		sqlServiceImpl.updateUserDetailById(updateId, newValues);
		verify(genericDao, times(1)).update(em -> em.find(UserDetail.class, updateId),
				toUpdate -> sqlServiceImpl.updateUserDetailByUserDetail(toUpdate, newValues));
	}

	@Test
	void shouldDeleteById() {
		sqlServiceImpl = new SqlServiceImpl(genericDao);
		UserDetail userDetail = new UserDetail();
		userDetail.setId(1);

		when(genericDao.find(UserDetail.class, 1)).thenReturn(userDetail);
		sqlServiceImpl.removeById(1);
		verify(genericDao, times(1)).remove(userDetail);
	}

	@Test
	void shouldDeleteByName() {
		sqlServiceImpl = new SqlServiceImpl(genericDao);
		UserDetail userDetail = new UserDetail();
		Login login = new Login();
		login.setUserDetail(userDetail);
		when(genericDao.selectSingle(Login.class, "SELECT l FROM Login l WHERE l.userName=?1", "bos"))
				.thenReturn(login);
		sqlServiceImpl.removeByUserName("bos");
		verify(genericDao, times(1)).remove(userDetail);
	}

}

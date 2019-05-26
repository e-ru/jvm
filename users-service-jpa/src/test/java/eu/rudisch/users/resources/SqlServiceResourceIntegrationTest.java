package eu.rudisch.users.resources;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import eu.rudisch.users.TestUtils;
import eu.rudisch.users.persistance.SqlService;
import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

public class SqlServiceResourceIntegrationTest implements SqlService {

	List<UserDetail> userDetails = new ArrayList<>();
	List<Role> roles = new ArrayList<>();
	List<Account> accounts = new ArrayList<>();

	public SqlServiceResourceIntegrationTest() {
		userDetails.add(TestUtils.creatUserDetail("bos", "bob@smith.com", getAccount("internal"), getRole("user")));
		userDetails.add(TestUtils.creatUserDetail("bos2", "bob@smith.com", getAccount("internal"), getRole("user")));

		Role role = new Role();
		role.setRole("user");
		roles.add(role);
		role = new Role();
		role.setRole("admin");
		roles.add(role);

		Account account = new Account();
		account.setName("internal");
		accounts.add(account);
		account = new Account();
		account.setName("external");
		accounts.add(account);
	}

	@Override
	public int insertUserDetail(UserDetail userDetail) {
		userDetail.setId(1);
		return userDetail.getId();
	}

	@Override
	public UserDetail getUserDetailById(int i) {
		UserDetail userDetail = TestUtils.creatUserDetail("bos", "bob@smith.com", getAccount("internal"),
				getRole("user"));
		userDetail.setId(i);
		return userDetail;
	}

	@Override
	public UserDetail getUserDetailByUserName(String userName) {
		return TestUtils.creatUserDetail(userName, "bob@smith.com", getAccount("internal"), getRole("user"));
	}

	@Override
	public List<UserDetail> getUserDetails() {
		return userDetails;
	}

	@Override
	public int insertAccount(Account account) {
		account.setId(1);
		return account.getId();
	}

	@Override
	public int insertRole(Role role) {
		role.setId(1);
		return role.getId();
	}

	@Override
	public Role getRole(String roleName) {
		Role role = new Role();
		role.setRole(roleName);
		return role;
	}

	@Override
	public Account getAccount(String accountName) {
		Account account = new Account();
		account.setName(accountName);
		return account;
	}

	@Override
	public List<Role> getRoles() {
		return roles;
	}

	@Override
	public UserDetail updateUserDetailById(int userDetailId, UserDetail userDetail) {
		UserDetail toUpdate = getUserDetailById(userDetailId);
		userDetail.getMembership().getAccounts().forEach(account -> toUpdate.getMembership().addAccount(account));
		userDetail.getMembership().getRoles().forEach(role -> toUpdate.getMembership().addRole(role));
		toUpdate.getMembership().setAccountEmailAddress(userDetail.getMembership().getAccountEmailAddress());

		return toUpdate;
	}

	@Override
	public UserDetail updateUserDetailByUserName(String userName, UserDetail userDetail) {
		UserDetail toUpdate = getUserDetailByUserName(userName);
		userDetail.getMembership().getAccounts().forEach(account -> toUpdate.getMembership().addAccount(account));
		userDetail.getMembership().getRoles().forEach(role -> toUpdate.getMembership().addRole(role));
		toUpdate.getMembership().setAccountEmailAddress(userDetail.getMembership().getAccountEmailAddress());

		return toUpdate;
	}

	@Override
	public void removeById(int i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeByUserName(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public EntityManager getEntityManager() {
		// TODO Auto-generated method stub
		return null;
	}

}

package eu.rudisch.users.resources;

import java.util.ArrayList;
import java.util.List;

import eu.rudisch.users.TestUtils;
import eu.rudisch.users.persistance.SqlService;
import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

public class SqlServiceResourceIntegrationTest implements SqlService {

	@Override
	public int insertUserDetail(UserDetail userDetail) {
		return 0;
	}

	@Override
	public UserDetail getUserDetailById(int i) {
		return null;
	}

	@Override
	public UserDetail getUserDetailByUserName(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserDetail> getUserDetails() {
		Role role = new Role();
		role.setRole("user");
		Account account = new Account();
		account.setName("internal");

		List<UserDetail> userDetails = new ArrayList<>();
		userDetails.add(TestUtils.creatUserDetail("bos", "bob@smith.com", account, role));
		userDetails.add(TestUtils.creatUserDetail("bos2", "bob@smith.com", account, role));
		return userDetails;
	}

	@Override
	public int insertAccount(Account account) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertRole(Role role) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Role getRole(String roleName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Account getAccount(String accountName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Role> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDetail updateUserDetailById(int userDetailId, UserDetail userDetail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDetail updateUserDetailByUserName(String userName, UserDetail userDetail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeById(int i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeByUserName(String userName) {
		// TODO Auto-generated method stub

	}

}

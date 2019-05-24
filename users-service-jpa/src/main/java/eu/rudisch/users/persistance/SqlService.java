package eu.rudisch.users.persistance;

import java.util.List;

import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

public interface SqlService {
	int insertUserDetail(UserDetail userDetail);

	UserDetail getUserDetailById(int i);

	UserDetail getUserDetailByUserName(String userName);

	List<UserDetail> getUserDetails();

	int insertAccount(Account account);

	int insertRole(Role role);

	Role getRole(String roleName);

	Account getAccount(String accountName);

	List<Role> getRoles();

	UserDetail updateUserDetailById(int userDetailId, UserDetail userDetail);

	UserDetail updateUserDetailByUserName(String userName, UserDetail userDetail);

	void removeById(int i);

	void removeByUserName(String userName);
}

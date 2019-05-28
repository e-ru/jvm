package eu.rudisch.users.persistance;

import java.util.List;

import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Login;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

public class SqlServiceImpl implements SqlService {

	private GenericDao genericDao;

	private static String unitName = "users-service-jpa";

	public SqlServiceImpl() {
		this(unitName);
	}

	public SqlServiceImpl(String unitName) {
		this(new GenericDao(unitName));
	}

	public SqlServiceImpl(GenericDao userDetailDao) {
		this.genericDao = userDetailDao;
	}

	@Override
	public int insertUserDetail(UserDetail userDetail) {
		return genericDao.insert(userDetail).getId();
	}

	@Override
	public int insertAccount(Account account) {
		return genericDao.insert(account).getId();
	}

	@Override
	public int insertRole(Role role) {
		return genericDao.insert(role).getId();
	}

	@Override
	public UserDetail getUserDetailById(int i) {
		return genericDao.find(UserDetail.class, i);
	}

	@Override
	public UserDetail getUserDetailByUserName(String userName) {
		UserDetail userDetail;
		try {
			Login login = genericDao.selectSingle(Login.class,
					"SELECT l FROM Login l WHERE l.userName=?1",
					userName);
			userDetail = login.getUserDetail();
		} catch (Exception e) {
			// TODO logging...
			userDetail = null;
		}
		return userDetail;
	}

	@Override
	public Role getRole(String roleName) {
		return genericDao.selectSingle(Role.class, "SELECT r FROM Role r WHERE r.role=?1", roleName);
	}

	@Override
	public Account getAccount(String accountName) {
		return genericDao.selectSingle(Account.class, "SELECT a FROM Account a WHERE a.name=?1", accountName);
	}

	@Override
	public List<UserDetail> getUserDetails() {
		return genericDao.selectList(UserDetail.class, "SELECT u FROM UserDetail u");
	}

	@Override
	public List<Role> getRoles() {
		return genericDao.selectList(Role.class, "SELECT r FROM Role r");
	}

	@Override
	public UserDetail updateUserDetailById(int userDetailId, UserDetail userDetail) {
		return genericDao.update(updateUserDetailByUserDetail(getUserDetailById(userDetailId), userDetail));
	}

	@Override
	public UserDetail updateUserDetailByUserName(String userName, UserDetail userDetail) {
		return genericDao.update(updateUserDetailByUserDetail(getUserDetailByUserName(userName), userDetail));
	}

	static UserDetail updateUserDetailByUserDetail(UserDetail toUpdate, UserDetail newValue) {
		newValue.getMembership().getAccounts().forEach(account -> toUpdate.getMembership().addAccount(account));
		newValue.getMembership().getRoles().forEach(role -> toUpdate.getMembership().addRole(role));
		toUpdate.getMembership().setAccountEmailAddress(newValue.getMembership().getAccountEmailAddress());
		return toUpdate;
	}

	@Override
	public void removeById(int i) {
		UserDetail userDetail = getUserDetailById(i);
		genericDao.remove(userDetail);
	}

	@Override
	public void removeByUserName(String userName) {
		UserDetail userDetail = getUserDetailByUserName(userName);
		genericDao.remove(userDetail);
	}
}

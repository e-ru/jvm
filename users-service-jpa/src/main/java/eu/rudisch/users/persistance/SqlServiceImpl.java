package eu.rudisch.users.persistance;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Login;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

public class SqlServiceImpl implements SqlService {

	private UserDetailDao userDetailDao;

	private EntityManagerFactory factory;

	private static String unitName = "users-service-jpa";

	public SqlServiceImpl() {
		this(unitName);
	}

	public SqlServiceImpl(String unitName) {
		this.userDetailDao = new UserDetailDao(unitName);
//		this.factory = Persistence.createEntityManagerFactory(unitName);
	}

	@Override
	public EntityManager getEntityManager() {
		return factory.createEntityManager();
	}

	@Override
	public int insertUserDetail(UserDetail userDetail) {
		return insert(userDetail).getId();
	}

	@Override
	public UserDetail getUserDetailById(int i) {
		EntityManager em = getEntityManager();
		UserDetail userDetail = em.find(UserDetail.class, i);
		em.close();

		return userDetail;
	}

	@Override
	public UserDetail getUserDetailByUserName(String userName) {
		UserDetail userDetail;
		try {
			userDetail = selectSingle(Login.class,
					"SELECT l FROM Login l WHERE l.userName=?1",
					userName).getUserDetail();
		} catch (Exception e) {
			// TODO logging...
			userDetail = null;
		}
		return userDetail;
	}

	@Override
	public List<UserDetail> getUserDetails() {
		return selectList(UserDetail.class, "SELECT u FROM UserDetail u");
	}

	@Override
	public int insertAccount(Account account) {
		return insert(account).getId();
	}

	@Override
	public int insertRole(Role role) {
		return insert(role).getId();
	}

	@Override
	public Role getRole(String roleName) {
		return selectSingle(Role.class, "SELECT r FROM Role r WHERE r.role=?1", roleName);
	}

	@Override
	public Account getAccount(String accountName) {
		return selectSingle(Account.class, "SELECT a FROM Account a WHERE a.name=?1", accountName);
	}

	@Override
	public List<Role> getRoles() {
		return selectList(Role.class, "SELECT r FROM Role r");
	}

	@Override
	public UserDetail updateUserDetailById(int userDetailId, UserDetail userDetail) {
		return update(em -> em.find(UserDetail.class, userDetailId),
				toUpdate -> updateUserDetailByUserDetail(toUpdate, userDetail));
	}

	@Override
	public UserDetail updateUserDetailByUserName(String userName, UserDetail userDetail) {
		return update(() -> getUserDetailByUserName(userName),
				toUpdate -> updateUserDetailByUserDetail(toUpdate, userDetail));
	}

	UserDetail updateUserDetailByUserDetail(UserDetail toUpdate, UserDetail newValue) {
		newValue.getMembership().getAccounts().forEach(account -> toUpdate.getMembership().addAccount(account));
		newValue.getMembership().getRoles().forEach(role -> toUpdate.getMembership().addRole(role));
		toUpdate.getMembership().setAccountEmailAddress(newValue.getMembership().getAccountEmailAddress());
		return toUpdate;
	}

	@Override
	public void removeById(int i) {
		UserDetail userDetail = getUserDetailById(i);
		userDetailDao.remove(userDetail);
	}

	@Override
	public void removeByUserName(String userName) {
		UserDetail userDetail = getUserDetailByUserName(userName);
		remove(userDetail);
	}
}

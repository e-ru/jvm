package eu.rudisch.users.persistance;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Login;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

public class SqlServiceImpl implements SqlService {

	private EntityManagerFactory factory;

	public SqlServiceImpl(Supplier<EntityManagerFactory> createFactory) {
		factory = createFactory.get();
	}

	public SqlServiceImpl(String unitName) {
		this.factory = Persistence.createEntityManagerFactory(unitName);
	}

	public SqlServiceImpl(FactoryCreator factoryCreator) {
		this.factory = factoryCreator.createFactory();
	}

	<T> T insert(T t) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.persist(t);
		em.getTransaction().commit();
		em.close();
		return t;
	}

	<T> TypedQuery<T> getTypedQuery(EntityManager em, Class<T> clazz, String q, Object... params) {
		TypedQuery<T> query = em.createQuery(q, clazz);
		IntStream.range(0, params.length)
				.forEach(i -> query.setParameter(i + 1, params[i]));
		return query;
	}

	<T> T selectSingle(Class<T> clazz, String q, Object... params) {
		EntityManager em = factory.createEntityManager();
		TypedQuery<T> query = getTypedQuery(em, clazz, q, params);
		T t = query.getSingleResult();
		em.close();
		return t;
	}

	<T> List<T> selectList(Class<T> clazz, String q, Object... params) {
		EntityManager em = factory.createEntityManager();
		TypedQuery<T> query = getTypedQuery(em, clazz, q, params);
		List<T> list = query.getResultList();
		em.close();
		return list;
	}

	<T> void remove(T t) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		if (!em.contains(t)) {
			t = em.merge(t);
		}
		em.remove(t);
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public int insertUserDetail(UserDetail userDetail) {
		return insert(userDetail).getId();
	}

	@Override
	public UserDetail getUserDetailById(int i) {
		EntityManager em = factory.createEntityManager();
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
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		UserDetail toUpdate = em.find(UserDetail.class, userDetailId);
		userDetail.getMembership().getAccounts().forEach(account -> toUpdate.getMembership().addAccount(account));
		userDetail.getMembership().getRoles().forEach(role -> toUpdate.getMembership().addRole(role));
		toUpdate.getMembership().setAccountEmailAddress(userDetail.getMembership().getAccountEmailAddress());
		em.getTransaction().commit();
		em.close();

		return toUpdate;
	}

	@Override
	public UserDetail updateUserDetailByUserName(String userName, UserDetail userDetail) {
		UserDetail toUpdate = getUserDetailByUserName(userName);

		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		userDetail.getMembership().getAccounts().forEach(account -> toUpdate.getMembership().addAccount(account));
		userDetail.getMembership().getRoles().forEach(role -> toUpdate.getMembership().addRole(role));
		toUpdate.getMembership().setAccountEmailAddress(userDetail.getMembership().getAccountEmailAddress());
		em.getTransaction().commit();
		em.close();

		return toUpdate;
	}

	@Override
	public void removeById(int i) {
		UserDetail userDetail = getUserDetailById(i);
		remove(userDetail);
	}

	@Override
	public void removeByUserName(String userName) {
		UserDetail userDetail = getUserDetailByUserName(userName);
		remove(userDetail);
	}

	public static SqlServiceImpl make(FactoryCreator creator) {
		return new SqlServiceImpl(creator);
	}
}

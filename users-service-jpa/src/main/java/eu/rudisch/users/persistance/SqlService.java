package eu.rudisch.users.persistance;

import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Login;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

public class SqlService {

	private EntityManagerFactory factory;

	public SqlService(String unitName) {
		factory = Persistence.createEntityManagerFactory(unitName);
	}

	public int insertUserDetail(UserDetail userDetail) {
		return insert(userDetail).getId();
	}

	public UserDetail getUserDetailById(int i) {
		EntityManager em = factory.createEntityManager();
		UserDetail userDetail = em.find(UserDetail.class, 1);

		return userDetail;
	}

	public UserDetail getUserDetailByUserName(String userName) {
		return singleSelect(Login.class,
				"SELECT l FROM Login l WHERE l.userName=?1",
				userName).getUserDetail();
	}

	public List<UserDetail> getUserDetails() {
		EntityManager em = factory.createEntityManager();
		TypedQuery<UserDetail> query = em.createQuery("SELECT u FROM UserDetail u", UserDetail.class);
		List<UserDetail> results = query.getResultList();
		em.close();

		return results;
	}

	public int insertAccount(Account account) {
		return insert(account).getId();
	}

	public int insertRole(Role role) {
		return insert(role).getId();
	}

	public Role getRole(String roleName) {
		return singleSelect(Role.class, "SELECT r FROM Role r WHERE r.role=?1", roleName);
	}

	public Account getAccount(String accountName) {
		return singleSelect(Account.class, "SELECT a FROM Account a WHERE a.name=?1", accountName);
	}

	<T> T insert(T t) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.persist(t);
		em.getTransaction().commit();
		em.close();
		return t;
	}

	<T> T singleSelect(Class<T> clazz, String q, Object... params) {
		EntityManager em = factory.createEntityManager();
		TypedQuery<T> query = em.createQuery(q, clazz);
		IntStream.range(0, params.length)
				.forEach(i -> query.setParameter(i + 1, params[i]));
		T t = query.getSingleResult();
		return t;
	}
}

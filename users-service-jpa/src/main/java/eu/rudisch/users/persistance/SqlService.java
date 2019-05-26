package eu.rudisch.users.persistance;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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

	EntityManager getEntityManager();

	default <T> T insert(T t) {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.persist(t);
		em.getTransaction().commit();
		em.close();
		return t;
	}

	default <T> TypedQuery<T> getTypedQuery(EntityManager em, Class<T> clazz, String q, Object... params) {
		TypedQuery<T> query = em.createQuery(q, clazz);
		IntStream.range(0, params.length)
				.forEach(i -> query.setParameter(i + 1, params[i]));
		return query;
	}

	default <T> T selectSingle(Class<T> clazz, String q, Object... params) {
		EntityManager em = getEntityManager();
		TypedQuery<T> query = getTypedQuery(em, clazz, q, params);
		T t = query.getSingleResult();
		em.close();
		return t;
	}

	default <T> List<T> selectList(Class<T> clazz, String q, Object... params) {
		EntityManager em = getEntityManager();
		TypedQuery<T> query = getTypedQuery(em, clazz, q, params);
		List<T> list = query.getResultList();
		em.close();
		return list;
	}

	default <T> T update(Function<EntityManager, T> getToUpdate, Function<T, T> updateFunc) {
		EntityManager em = getEntityManager();
		T t = getToUpdate.apply(em);
		return applyUpdate(em, t, updateFunc);
	}

	default <T> T update(Supplier<T> getToUpdate, Function<T, T> updateFunc) {
		EntityManager em = getEntityManager();
		T t = getToUpdate.get();
		return applyUpdate(em, t, updateFunc);
	}

	default <T> T applyUpdate(EntityManager em, T t, Function<T, T> updateFunc) {
		em.getTransaction().begin();
		t = updateFunc.apply(t);
		em.getTransaction().commit();
		em.close();
		return t;
	}

	default <T> void remove(T t) {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		if (!em.contains(t)) {
			t = em.merge(t);
		}
		em.remove(t);
		em.getTransaction().commit();
		em.close();
	}

//	default void removeById(int i) {
//		UserDetail userDetail = getUserDetailById(i);
//		remove(userDetail);
//	}
}

package eu.rudisch.users.persistance;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class UserDetailDao {

	private static String unitName = "users-service-jpa";

	private EntityManagerFactory factory;

	public UserDetailDao() {
		this(unitName);
	}

	public UserDetailDao(String unitName) {
		this.factory = Persistence.createEntityManagerFactory(unitName);
	}

	public EntityManager getEntityManager() {
		return factory.createEntityManager();
	}

	<T> T insert(T t) {
		EntityManager em = getEntityManager();
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
		EntityManager em = getEntityManager();
		TypedQuery<T> query = getTypedQuery(em, clazz, q, params);
		T t = query.getSingleResult();
		em.close();
		return t;
	}

	<T> List<T> selectList(Class<T> clazz, String q, Object... params) {
		EntityManager em = getEntityManager();
		TypedQuery<T> query = getTypedQuery(em, clazz, q, params);
		List<T> list = query.getResultList();
		em.close();
		return list;
	}

	<T> T update(Function<EntityManager, T> getToUpdate, Function<T, T> updateFunc) {
		EntityManager em = getEntityManager();
		T t = getToUpdate.apply(em);
		return applyUpdate(em, t, updateFunc);
	}

	<T> T update(Supplier<T> getToUpdate, Function<T, T> updateFunc) {
		EntityManager em = getEntityManager();
		T t = getToUpdate.get();
		return applyUpdate(em, t, updateFunc);
	}

	<T> T applyUpdate(EntityManager em, T t, Function<T, T> updateFunc) {
		em.getTransaction().begin();
		t = updateFunc.apply(t);
		em.getTransaction().commit();
		em.close();
		return t;
	}

	<T> void remove(T t) {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		if (!em.contains(t)) {
			t = em.merge(t);
		}
		em.remove(t);
		em.getTransaction().commit();
		em.close();
	}
}

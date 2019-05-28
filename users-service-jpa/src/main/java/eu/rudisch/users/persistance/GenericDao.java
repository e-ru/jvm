package eu.rudisch.users.persistance;

import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class GenericDao {

	private EntityManagerFactory factory;

	public GenericDao(String unitName) {
		this.factory = Persistence.createEntityManagerFactory(unitName);
	}

	<T> T insert(T t) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.persist(t);
		em.getTransaction().commit();
		em.close();
		return t;
	}

	<T> T find(Class<T> clazz, int id) {
		EntityManager em = factory.createEntityManager();
		T t = em.find(clazz, id);
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

	<T> T update(T toUpdate) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		toUpdate = em.merge(toUpdate);
		em.getTransaction().commit();
		em.close();
		return toUpdate;
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
}

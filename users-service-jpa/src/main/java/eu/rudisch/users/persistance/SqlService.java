package eu.rudisch.users.persistance;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import eu.rudisch.users.persistance.model.UserDetail;

public class SqlService {

	private EntityManagerFactory factory;

	public SqlService(String unitName) {
		factory = Persistence.createEntityManagerFactory(unitName);
	}

	public void insertUserDetail(UserDetail userDetail) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.persist(userDetail);
		em.getTransaction().commit();
		em.close();
	}

	public List<UserDetail> getUserDetails() {
		EntityManager em = factory.createEntityManager();

		TypedQuery<UserDetail> query = em.createQuery(
				"SELECT u FROM UserDetail u", UserDetail.class);
		List<UserDetail> results = query.getResultList();

		em.close();

		return results;
	}
}

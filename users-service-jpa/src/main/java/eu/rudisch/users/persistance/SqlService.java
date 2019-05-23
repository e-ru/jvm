package eu.rudisch.users.persistance;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import eu.rudisch.users.persistance.model.Login;
import eu.rudisch.users.persistance.model.UserDetail;

public class SqlService {

	private EntityManagerFactory factory;

	public SqlService(String unitName) {
		factory = Persistence.createEntityManagerFactory(unitName);
	}

	public int insertUserDetail(UserDetail userDetail) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.persist(userDetail);
		em.getTransaction().commit();
		em.close();

		return userDetail.getId();
	}

	public UserDetail getUserDetailById(int i) {
		EntityManager em = factory.createEntityManager();
		UserDetail userDetail = em.find(UserDetail.class, 1);

		return userDetail;
	}

	public UserDetail getUserDetailByUserName(String userName) {
		EntityManager em = factory.createEntityManager();
		TypedQuery<Login> query = em.createQuery("SELECT l FROM Login l WHERE l.userName=:userName", Login.class);
		query.setParameter("userName", userName);
		Login login = query.getSingleResult();

		return login.getUserDetail();
	}

	public List<UserDetail> getUserDetails() {
		EntityManager em = factory.createEntityManager();
		TypedQuery<UserDetail> query = em.createQuery("SELECT u FROM UserDetail u", UserDetail.class);
		List<UserDetail> results = query.getResultList();
		em.close();

		return results;
	}
}

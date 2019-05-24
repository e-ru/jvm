package eu.rudisch.users.persistance;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class FactoryCreatorSqlIntegrationTest implements FactoryCreator {

	@Override
	public EntityManagerFactory createFactory() {
		return Persistence.createEntityManagerFactory("users-service-jpa-int");
	}

}

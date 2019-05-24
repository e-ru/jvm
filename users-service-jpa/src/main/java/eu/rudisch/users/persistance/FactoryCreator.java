package eu.rudisch.users.persistance;

import javax.persistence.EntityManagerFactory;

public interface FactoryCreator {

	EntityManagerFactory createFactory();
}

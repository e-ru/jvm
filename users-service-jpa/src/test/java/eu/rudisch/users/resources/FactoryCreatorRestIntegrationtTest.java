package eu.rudisch.users.resources;

import javax.persistence.EntityManagerFactory;

import eu.rudisch.users.persistance.FactoryCreator;

public class FactoryCreatorRestIntegrationtTest implements FactoryCreator {

	@Override
	public EntityManagerFactory createFactory() {
		return null;
	}

}

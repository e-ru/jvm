package eu.rudisch.authorizationserver.services;

import java.util.List;

import eu.rudisch.authorizationserver.model.UserRestRep;

public interface UserService {
	List<UserRestRep> getUserRepresentations();

	UserRestRep updateUser(int id, UserRestRep toUpdate, String issuer);
}

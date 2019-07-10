package eu.rudisch.authorizationadmin.service;

import java.util.List;

import eu.rudisch.authorizationadmin.web.controller.resource.UserRestRep;

public interface UserService {
	List<UserRestRep> getUserRepresentations();

	UserRestRep updateUser(int id, UserRestRep toUpdate, String issuer);
}

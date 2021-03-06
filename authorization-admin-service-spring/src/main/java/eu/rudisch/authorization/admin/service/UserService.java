package eu.rudisch.authorization.admin.service;

import java.util.List;

import eu.rudisch.authorization.admin.web.controller.resource.UserRestRep;

public interface UserService {
	List<UserRestRep> getUserRepresentations();

	UserRestRep updateUser(int id, UserRestRep toUpdate, String issuer);
}

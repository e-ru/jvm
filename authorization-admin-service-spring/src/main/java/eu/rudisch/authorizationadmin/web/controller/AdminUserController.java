package eu.rudisch.authorizationadmin.web.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.rudisch.authorizationadmin.service.UserService;
import eu.rudisch.authorizationadmin.web.controller.resource.UserRestRep;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
	private static final Logger LOGGER = LogManager.getLogger(AdminUserController.class);

	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<UserRestRep> getUsers() {
		LOGGER.info("Get users");
		return userService.getUserRepresentations();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody UserRestRep updateUser(@PathVariable("id") int id, @RequestBody UserRestRep toUpdate,
			Authentication authentication) {
		LOGGER.info(String.format("Update user with id: %d, user: %s", id, toUpdate.toString()));
		// resource server configuration manages access control - authentication should not be null
		return userService.updateUser(id, toUpdate, (String) authentication.getPrincipal());
	}
}

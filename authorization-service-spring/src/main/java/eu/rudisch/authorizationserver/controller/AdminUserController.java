package eu.rudisch.authorizationserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.rudisch.authorizationserver.model.User;
import eu.rudisch.authorizationserver.repository.UserRepository;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
	@Autowired
	private UserRepository userRepository;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<User> getUser() {
		List<User> users = userRepository.findAll();
		return users;
	}
}

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
import eu.rudisch.authorizationserver.repository.UserDetailRepository;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserDetailRepository userDetailRepository;

	@RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<User> getUser() {
		List<User> users = userDetailRepository.findAll();
		return users;
	}

	// #### test region ####

	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody StringResponse getTest() {
		return new StringResponse("Hello World from GET");
	}

	@RequestMapping(value = "/test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
	public @ResponseBody StringResponse postTest() {
		return new StringResponse("Hello World from POST");
	}

	@Getter
	@Setter
	private class StringResponse {
		private String response;

		public StringResponse(String s) {
			this.response = s;
		}
	}
}

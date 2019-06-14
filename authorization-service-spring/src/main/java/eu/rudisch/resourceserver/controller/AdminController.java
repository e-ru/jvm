package eu.rudisch.resourceserver.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("admin")
public class AdminController {

	@PreAuthorize("#oauth2.hasScope('READ')")
	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> test() {
//		return new ResponseEntity<>("Hello World", HttpStatus.OK);
		return ResponseEntity.ok("Hello World");
	}
}

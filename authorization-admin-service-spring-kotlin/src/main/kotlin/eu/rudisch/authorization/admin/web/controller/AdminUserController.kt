package eu.rudisch.authorization.admin.web.controller

import eu.rudisch.authorization.admin.service.UserService
import eu.rudisch.authorization.admin.web.controller.resource.UserRestRep
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/users")
class AdminUserController {
	private val logger = LogManager.getLogger(AdminUserController::class.java)

	@Autowired
	private val userService: UserService? = null

	@RequestMapping(method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	fun getUsers(): List<UserRestRep> {
		logger.info("Get users")
		return userService!!.getUserRepresentations()
	}

	@RequestMapping(value = ["/{id}"], method = [RequestMethod.PUT], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	fun updateUser(@PathVariable("id") id: Int, @RequestBody toUpdate: UserRestRep,
				   authentication: Authentication): UserRestRep {
		logger.info(String.format("Update user with id: %d, user: %s", id, toUpdate.toString()))
		// resource server configuration manages access control - authentication should not be null
		return userService!!.updateUser(id, toUpdate, authentication.principal as String)
	}
}
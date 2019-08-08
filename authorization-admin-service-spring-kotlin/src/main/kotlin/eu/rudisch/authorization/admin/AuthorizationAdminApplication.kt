package eu.rudisch.authorization.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthorizationAdminApplication

fun main(args: Array<String>) {
	runApplication<AuthorizationAdminApplication>(*args)
}

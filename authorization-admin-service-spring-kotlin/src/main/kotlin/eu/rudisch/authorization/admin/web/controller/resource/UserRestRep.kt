package eu.rudisch.authorization.admin.web.controller.resource

import eu.rudisch.authorization.admin.model.User
import java.util.stream.Collectors

data class UserRestRep(val id: Int,
					   val username: String,
					   val password: String,
					   val passwordRepeat: String,
					   val email: String,
					   val enabled: Boolean,
					   val accountExpired: Boolean,
					   val credentialsExpired: Boolean,
					   val accountLocked: Boolean,
					   val roleNames: List<String>) {

	companion object {
		@JvmStatic
		fun fromUser(user: User): UserRestRep {
			return UserRestRep(user.id,
					user.username,
					user.password,
					user.password,
					user.email,
					user.enabled,
					!user.accountNonExpired,
					!user.accountNonLocked,
					!user.credentialsNonExpired,
					user.roles.stream()
							.map { it.name }
							.collect(Collectors.toList<String>()))
		}
	}
}
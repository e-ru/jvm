package eu.rudisch.authorization.admin.web.controller.resource

import eu.rudisch.authorization.admin.model.User
import java.util.stream.Collectors

data class UserRestRep(var id: Int,
					   var username: String,
					   var password: String,
					   val passwordRepeat: String,
					   var email: String,
					   var enabled: Boolean,
					   var accountExpired: Boolean,
					   var credentialsExpired: Boolean,
					   var accountLocked: Boolean,
					   var roleNames: List<String>) {

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